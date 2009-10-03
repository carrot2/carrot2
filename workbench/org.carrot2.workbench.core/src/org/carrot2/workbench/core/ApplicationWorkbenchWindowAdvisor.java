
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import java.util.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.application.*;
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
        final Rectangle fullScreenSize = Display.getDefault().getPrimaryMonitor()
            .getClientArea();
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
        final double ratio = 0.8;
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
