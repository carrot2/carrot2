package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.core.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchInput;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A processing job runs a query specified by the {@link SearchInput} on an instance of
 * the {@link Controller} acquired from {@link WorkbenchCorePlugin}.
 */
public final class SearchJob extends Job
{
    /**
     * Result to be processed.
     */
    private final SearchResult searchResult;

    /**
     * Re-process the {@link SearchInput} associated with {@link SearchResult}, 
     * updating it if successful.
     */
    public SearchJob(SearchResult results)
    {
        super(results.getInput().getName());
        this.searchResult = results;
    }

    /**
     * Run this job
     */
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        IStatus status;
        monitor.beginTask("Executing", IProgressMonitor.UNKNOWN);
        try
        {
            final SearchInput searchInput = searchResult.getInput();
            final Map<String, Object> attributes = searchInput.getAttributeValueSet()
                .getAttributeValues();

            final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
            final Controller controller = core.getController();

            final ProcessingResult result = controller.process(attributes, core.getSources()
                .getImplementation(searchInput.getSourceId()).clazz, core.getAlgorithms()
                .getImplementation(searchInput.getAlgorithmId()).clazz);

            searchResult.setProcessingResult(result);
            status = Status.OK_STATUS;
        }
        catch (ProcessingException ex)
        {
            status = new Status(IStatus.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                "Processing error: " + ex.getMessage(), ex);
        }
        finally
        {
            monitor.done();
        }

        return status;
    }
}
