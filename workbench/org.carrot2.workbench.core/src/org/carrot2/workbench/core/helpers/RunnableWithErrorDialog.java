package org.carrot2.workbench.core.helpers;

import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

//TODO: try replacing this with Eclispe Jobs API
public abstract class RunnableWithErrorDialog implements Runnable
{

    public void run()
    {
        try
        {
            runCore();
        }
        catch (Throwable t)
        {
            final IStatus status = new OperationStatus(IStatus.ERROR,
                CorePlugin.PLUGIN_ID, -2, getErrorTitle(), t);
            CorePlugin.getDefault().getLog().log(status);
            if (Display.getCurrent() != null)
            {
                ErrorDialog.openError(Display.getDefault().getActiveShell(), null,
                    getErrorTitle(), status);
            }
            else
            {
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        ErrorDialog.openError(Display.getDefault().getActiveShell(),
                            null, null, status);
                    }
                });
            }
        }
    }

    protected abstract String getErrorTitle();

    protected abstract void runCore() throws Exception;

}
