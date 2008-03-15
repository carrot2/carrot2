package org.carrot2.workbench.core.helpers;

import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

public class Utils
{

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

    public static void logError(String message, Throwable exception, boolean showError)
    {
        IStatus status =
            new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -1, message,
                exception);
        CorePlugin.getDefault().getLog().log(status);
        if (showError)
        {
            showError(status);
        }
    }

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
}
