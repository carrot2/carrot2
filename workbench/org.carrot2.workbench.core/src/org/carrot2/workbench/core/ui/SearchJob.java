
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

package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.core.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;

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
     * 
     */
    public SearchJob(String title, SearchResult results)
    {
        super(title);
        this.searchResult = results;
    }

    /**
     * Constructs a search job, naming it after the
     * input's {@link SearchInput#getName()}.
     */
    public SearchJob(SearchResult results)
    {
        this(results.getInput().getName(), results);
    }
    
    /**
     * Run this job
     */
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        final SearchInput searchInput = searchResult.getInput();
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();

        final ProcessingComponentDescriptor source = core.getComponent(searchInput.getSourceId());
        final ProcessingComponentDescriptor algorithm = core.getComponent(searchInput.getAlgorithmId());
    
        IStatus status;
        monitor.beginTask("Processing: "
            + source.getLabel() + " -> " + algorithm.getLabel(), IProgressMonitor.UNKNOWN);

        try
        {
            final Map<String, Object> attributes = 
                searchInput.getAttributeValueSet().getAttributeValues();

            final Controller controller = core.getController();

            final ProcessingResult result = controller.process(
                attributes, source.getId(), algorithm.getId());

            PlatformUI.getWorkbench().getDisplay().syncExec(
                new Runnable()
                {
                    public void run()
                    {
                        searchResult.getInput().setAttribute(
                            CachingProcessingComponentManager.CACHE_BYPASS_ATTR, false, false);
                        searchResult.setProcessingResult(result);
                    }
                }
            );
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
