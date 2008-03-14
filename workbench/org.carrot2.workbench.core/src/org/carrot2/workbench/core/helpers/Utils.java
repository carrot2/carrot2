package org.carrot2.workbench.core.helpers;

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
