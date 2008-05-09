package org.carrot2.workbench.core.jobs;

import java.util.Map;

import org.carrot2.core.*;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.SearchParameters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class ProcessingJob extends Job
{
    public final ProcessingComponent source;
    public final ProcessingComponent algorithm;
    public final Map<String, Object> attributes;

    @SuppressWarnings("unchecked")
    public ProcessingJob(String name, SearchParameters search)
    {
        super(name);
        // TODO: I only need classes, not instances, figure out how to do this
        source =
            ComponentLoader.SOURCE_LOADER.getExecutableComponent(search.getSourceId());
        algorithm =
            ComponentLoader.ALGORITHM_LOADER.getExecutableComponent(search
                .getAlgorithmId());
        attributes = search.getAttributes();
        try
        {
            AttributeBinder.bind(algorithm, attributes, Input.class, Processing.class);
        }
        catch (AttributeBindingException e)
        {
            Utils.logError(e, false);
        }
        catch (InstantiationException e)
        {
            Utils.logError(e, false);
        }
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
                controller.process(attributes, source.getClass(), algorithm.getClass());
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
