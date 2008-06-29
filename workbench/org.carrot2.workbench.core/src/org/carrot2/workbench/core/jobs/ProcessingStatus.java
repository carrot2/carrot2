package org.carrot2.workbench.core.jobs;

import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ProcessingStatus extends Status
{

    /**
     * Result of the processing (not null only when code is OK).
     */
    public ProcessingResult result;

    /**
     * Used when processing ended with exception. Severity is assumed to be ERROR.
     * 
     * @param exception
     */
    public ProcessingStatus(Throwable exception)
    {
        super(IStatus.ERROR, WorkbenchCorePlugin.PLUGIN_ID, -3, exception.getMessage(), exception);
    }

    /**
     * Used when processing ended with success.
     * 
     * @param result
     */
    public ProcessingStatus(ProcessingResult result)
    {
        super(IStatus.OK, WorkbenchCorePlugin.PLUGIN_ID, "");
        this.result = result;
    }

}
