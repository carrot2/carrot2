package org.carrot2.workbench.core.jobs;

import java.util.Map;

import org.carrot2.core.*;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.carrot2.workbench.core.helpers.ComponentWrapper;
import org.carrot2.workbench.core.ui.SearchParameters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class ProcessingJob extends Job
{
    public final ComponentWrapper source;
    public final ComponentWrapper algorithm;
    public final Map<String, Object> attributes;

    public ProcessingJob(String name, SearchParameters search)
    {
        super(name);
        source = ComponentLoader.SOURCE_LOADER.getComponent(search.getSourceId());
        algorithm =
            ComponentLoader.ALGORITHM_LOADER.getComponent(search.getAlgorithmId());
        attributes = search.getAttributes();
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        IStatus status;
        monitor.beginTask("Processing of a query", IProgressMonitor.UNKNOWN);
        try
        {
            final Controller controller = CorePlugin.getController();
            final ProcessingResult result =
                controller.process(attributes, source.getComponentClass(), algorithm
                    .getComponentClass());
            status = new ProcessingStatus(result);
        }
        catch (ProcessingException ex)
        {
            status = new ProcessingStatus(ex);
        }
        monitor.done();
        return status;
    }
}
