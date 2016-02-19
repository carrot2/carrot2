
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

package org.carrot2.workbench.core.helpers;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.slf4j.LoggerFactory;

/*
 * 
 */
public final class Utils
{
    private Utils()
    {
        // no instances.
    }

    /**
     * Shows dialog with error, message will be taken from status.
     * 
     * @param status
     */
    public static void showError(final IStatus status)
    {
        showError(status.getMessage(), status);
    }

    /**
     * Shows dialog with error.
     * 
     * @param message
     * @param status
     */
    public static void showError(final String message, final IStatus status)
    {
        WorkbenchCorePlugin.getDefault().getLog().log(status);

        if (Display.getCurrent() != null)
        {
            ErrorDialog.openError(Display.getDefault().getActiveShell(), null, message,
                status);
        }
        else
        {
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    ErrorDialog.openError(Display.getDefault().getActiveShell(), null,
                        message, status);
                }
            });
        }
    }

    /*
     * 
     */
    public static void logError(String message, Throwable exception, boolean showError)
    {
        LoggerFactory.getLogger(Utils.class).error(message, exception);

        IStatus status = new Status(IStatus.ERROR,
            WorkbenchCorePlugin.PLUGIN_ID, -1, message, exception);
        WorkbenchCorePlugin.getDefault().getLog().log(status);
        if (showError)
        {
            showError(status);
        }
        else
        {
            try
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                    "org.eclipse.pde.runtime.LogView");
            }
            catch (Exception e)
            {
                // Don't do anything.
            }
        }
    }

    /*
     * 
     */
    public static void logError(String message, boolean showError)
    {
        logError(message, null, showError);
    }

    /*
     * 
     */
    public static void logInfo(String message)
    {
        IStatus status = new Status(IStatus.INFO,
            WorkbenchCorePlugin.PLUGIN_ID, message);
        WorkbenchCorePlugin.getDefault().getLog().log(status);        
    }
    
    /*
     * 
     */
    public static void logError(Throwable exception, boolean showError)
    {
        logError(exception.getMessage(), exception, showError);
    }

    /**
     * Utility method, the same as <code>Display.getDefault().asyncExec(runnable);</code>
     * 
     * @param runnable
     */
    public static void asyncExec(Runnable runnable)
    {
        Display.getDefault().asyncExec(runnable);
    }

    /**
     * Returns active editor part or <code>null</code> if not found.
     */
    public static IEditorPart getActiveEditor()
    {
        final IWorkbenchWindow wb = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (wb == null) return null;

        final IWorkbenchPage page = wb.getActivePage();
        if (page == null) return null;

        final IEditorPart editor = page.getActiveEditor();
        return editor;
    }

    /**
     * Calls {@link FormToolkit#adapt(Control, boolean, boolean)} for given control. If
     * <code>control</code> is an instance of {@link Composite}, this method is called
     * recursively for all the children.
     */
    public static void adaptToFormUI(FormToolkit toolkit, Control control)
    {
        if (control instanceof Composite)
        {
            final Composite c = (Composite) control;
            toolkit.adapt(c);

            final Control [] children = c.getChildren();
            for (int i = 0; i < children.length; i++)
            {
                adaptToFormUI(toolkit, children[i]);
            }
        }
        else
        {
            toolkit.adapt(control, true, true);
        }
    }

    /**
     * Set the given component's background color to a given system color.
     */
    public static void setBackground(Control c, int systemColor)
    {
        c.setBackground(
            PlatformUI.getWorkbench().getDisplay().getSystemColor(systemColor));
    }

    /**
     * Show a given view in the workbench if it exists.
     */
    public static IViewPart showView(String viewID)
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();

        if (page != null)
        {
            try
            {
                IViewPart view2 = page.findView(viewID);
                if (!page.isPartVisible(view2))
                {
                    return page.showView(viewID);
                }
            }
            catch (PartInitException e)
            {
                // Ignore part init exceptions.
            }
        }
        
        return null;
    }
}
