
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.actions.DisplayWelcomeView;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.views.IViewDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures various aspects of the main application's window.
 */
final class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
    private final static Logger logger = LoggerFactory.getLogger(ApplicationWorkbenchWindowAdvisor.class);

    /*
     * 
     */
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
    {
        super(configurer);
    }

    /*
     * 
     */
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor(configurer);
    }

    /*
     * 
     */
    public void preWindowOpen()
    {
        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        final Monitor primary = Display.getDefault().getPrimaryMonitor();

        /*
         * Get the bounds of the primary monitor, not its client area.
         * 
         * Client area on multi-monitor desktops in Linux (Xinerama) spans across all monitors,
         * but the window manager won't allow the window to take all that space immediately
         * (at least that's the effect I'm observing on my desktop).
         * 
         * What's funny is that the window still opens on monitor[0], but is shifted
         * outside of its bounds (and thus spills to monitor[1]). Seems like getPrimaryMonitor()
         * is not used when creating the Workbench shell.
         */
        final Rectangle fullScreenSize = primary.getBounds();
        int width = calculateInitialSize(fullScreenSize.width, 800);
        int height = calculateInitialSize(fullScreenSize.height, 600);
        configurer.setInitialSize(new Point(width, height));

        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowCoolBar(true);
    }

    /*
     * 
     */
    @Override
    public void postWindowCreate()
    {
        /*
         * Log contribution identifiers in case we need to remove them using the
         * activities API (declarative extension point in plugin.xml).
         */
        logContributionIdentifiers();

        // Hack for CARROT-1088 (perspective switcher ignores previous setting of
        // org.eclipse.ui/PERSPECTIVE_BAR_EXTRAS
        IWorkbenchWindow window = getWindowConfigurer().getWindow();
        IPerspectiveRegistry perspectiveRegistry = window.getWorkbench().getPerspectiveRegistry();
        for (String perspectiveId : new String [] {
            "org.carrot2.workbench.core.perspective.visualization",
            "org.carrot2.workbench.core.perspective.tuning",
            "org.carrot2.workbench.core.perspective.search",
        }) {
          window.getActivePage().setPerspective(
              perspectiveRegistry.findPerspectiveWithId(perspectiveId));
        }
    }

    /**
     * Log contribution identifiers from various extension points (that we may wish to
     * hide using the activities API).
     */
    @SuppressWarnings("unchecked")
    private void logContributionIdentifiers()
    {
        final StringBuilder b = new StringBuilder();

        /*
         * Toolbar contributions.
         */
        final ICoolBarManager cm = getWindowConfigurer().getActionBarConfigurer()
            .getCoolBarManager();

        b.append("Toolbar contributions:\n");
        for (IContributionItem item : cm.getItems())
        {
            b.append(item.getId() + " (" + item.getClass().getSimpleName() + ")");
            b.append("\n");
        }

        logger.debug(b.toString());
        b.setLength(0);

        /*
         * Menu contributions.
         */
        final Stack<IContributionItem> mms = new Stack<IContributionItem>();
        mms.push(getWindowConfigurer().getActionBarConfigurer().getMenuManager());

        b.append("Menu contributions:\n");
        while (!mms.isEmpty())
        {
            final IContributionItem item = mms.pop();

            b.append(item.getId() + " (" + item.getClass().getSimpleName() + ")");
            b.append("\n");

            if (item instanceof IMenuManager)
            {
                IMenuManager mmgr = (IMenuManager) item;
                mms.addAll(Arrays.asList(mmgr.getItems()));
            }
        }

        logger.debug(b.toString());
        b.setLength(0);

        /*
         * Preference pages.
         */
        b.append("Preference page contributions:\n");
        final PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager();

        List<?> elements = pm.getElements(PreferenceManager.PRE_ORDER);
        for (IPreferenceNode pref : (List<IPreferenceNode>) elements)
        {
            b.append(pref.getId() + " (" + pref.getLabelText() + ")\n");
        }

        logger.debug(b.toString());
        b.setLength(0);

        /*
         * Views.
         */
        b.append("View contributions:\n");
        for (IViewDescriptor view : getWindowConfigurer().getWorkbenchConfigurer()
            .getWorkbench().getViewRegistry().getViews())
        {
            b.append(view.getId() + " (" + view.getLabel() + ")");
            b.append("\n");
        }

        logger.debug(b.toString());
        b.setLength(0);
    }

    /*
     * 
     */
    @Override
    public void postWindowOpen()
    {
        super.postWindowOpen();

        /*
         * After the Workbench window is opened we eagerly re-activate editors to
         * initialize tab icons.
         */
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
        {
            for (IEditorReference editor : window.getActivePage().getEditorReferences())
            {
                editor.getEditor(true);
            }
        }
        
        /*
         * In case of component loading errors, display an error message and open the error log. 
         */
        WorkbenchCorePlugin wbCore = WorkbenchCorePlugin.getDefault();
        final List<ProcessingComponentDescriptor> failed 
            = wbCore.getFailed();
        if (!failed.isEmpty())
        {
            final StringBuilder errorMessages = new StringBuilder();
            for (ProcessingComponentDescriptor p : failed)
            {
                Throwable t = p.getInitializationFailure();

                errorMessages.append(p.getTitle() + ": " + t.getClass().getName());
                if (t.getMessage() != null && t.getMessage().length() > 0)
                    errorMessages.append("\n   " + t.getMessage());
                errorMessages.append("\n\n");
            }

            Utils.showView("org.eclipse.pde.runtime.LogView");
            ErrorDialog.openError(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                "Fatal errors", 
                "Plugin loading errors. See the error log for details.", 
                new Status(Status.ERROR, WorkbenchCorePlugin.PLUGIN_ID, errorMessages.toString()));
        }

        /*
         * Open the welcome view on first execution.
         */
        if (!WorkbenchCorePlugin.getPreferences().getBoolean(
            DisplayWelcomeView.ALREADY_DISPLAYED, false))
        {
            new DisplayWelcomeView().execute();
        }
    }

    /**
     * Calculates specified ratio of fullScreenSize (currently 80%) in such a way, that
     * result is not smaller than minSize. Calculates one coordinate only.
     * 
     * @param fullScreenSize size of full screen
     * @param minSize minimal wanted size
     * @return initial size for the workbench window
     */
    private int calculateInitialSize(int fullScreenSize, int minSize)
    {
        int size;
        final double ratio = 0.9;
        if ((int) (fullScreenSize * ratio) >= minSize)
        {
            size = (int) (fullScreenSize * ratio);
        }
        else
        {
            if (fullScreenSize >= minSize)
            {
                size = minSize;
            }
            else
            {
                size = fullScreenSize;
            }
        }
        return size;
    }
}
