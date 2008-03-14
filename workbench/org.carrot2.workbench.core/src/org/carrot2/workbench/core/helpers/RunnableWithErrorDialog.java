package org.carrot2.workbench.core.helpers;

import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;

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
            final IStatus status =
                new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -2,
                    getErrorTitle(), t);
            CorePlugin.getDefault().getLog().log(status);
            Utils.showError(getErrorTitle(), status);
        }
    }

    protected abstract String getErrorTitle();

    protected abstract void runCore() throws Exception;

}
