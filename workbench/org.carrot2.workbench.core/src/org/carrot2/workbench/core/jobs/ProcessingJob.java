package org.carrot2.workbench.core.jobs;

import java.util.Map;

import org.carrot2.core.*;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.carrot2.workbench.core.ui.SearchParameters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class ProcessingJob extends Job
{
    private ProcessingComponent source;
    private ProcessingComponent algorithm;
    private Map<String, Object> attributes;

    public ProcessingJob(String name, SearchParameters search)
    {
        super(name);
        source = ComponentLoader.SOURCE_LOADER.getComponent(search.getSourceCaption());
        algorithm =
            ComponentLoader.ALGORITHM_LOADER.getComponent(search.getAlgorithmCaption());
        attributes = search.getAttributes();
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        IStatus status;
        monitor.beginTask("Processing of a query", 1);
        try
        {
            final SimpleController controller = new SimpleController();
            final ProcessingResult result =
                controller.process(attributes, source, algorithm);
            status = new ProcessingStatus(result);
        }
        catch (ProcessingException ex)
        {
            status = new ProcessingStatus(ex);
        }
        monitor.worked(1);
        monitor.done();
        return status;
    }
}
