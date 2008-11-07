
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.core.Controller;
import org.carrot2.core.ProcessingResult;
import org.eclipse.ui.PlatformUI;

/**
 * Search process model is the core model around which all other views revolve (editors,
 * views, actions). It can perform transformation of {@link SearchInput} into a
 * {@link ProcessingResult} and inform its listeners about changes going on in the model.
 */
public final class SearchResult
{
    /**
     * Input for this result.
     */
    private final SearchInput input;

    /**
     * Processing result from a {@link Controller}, associated with {@link #input}.
     */
    private ProcessingResult result;

    /**
     * An array of listeners interested in events happening on this search result.
     */
    private List<ISearchResultListener> listeners = new CopyOnWriteArrayList<ISearchResultListener>();

    /**
     * Dispatch {@link ISearchResultListener#processingResultUpdated(ProcessingResult)}
     * to listeners.
     */
    final Runnable dispatchResultUpdated = new Runnable()
    {
        public void run()
        {
            for (ISearchResultListener listener : listeners)
            {
                listener.processingResultUpdated(result);
            }
        }
    };

    /**
     * 
     */
    public SearchResult(SearchInput input)
    {
        this.input = input;
    }

    /*
     * 
     */
    public SearchInput getInput()
    {
        return input;
    }

    /**
     * Update {@link ProcessingResult} associated with this object, notifying all
     * interested listeners.
     */
    public void setProcessingResult(ProcessingResult result)
    {
        this.result = result;
        fireProcessingResultUpdated();
    }

    /**
     * Returns the current processing result or <code>null</code> if not available.
     */
    public ProcessingResult getProcessingResult()
    {
        return this.result;
    }

    /*
     * 
     */
    public void addListener(ISearchResultListener listener)
    {
        this.listeners.add(listener);
    }

    /*
     * 
     */
    public void removeListener(ISearchResultListener listener)
    {
        this.listeners.remove(listener);
    }

    /**
     * Asynchronously fire processing results updated event.
     */
    private void fireProcessingResultUpdated()
    {
        /*
         * If we have a running Workbench (which should always be the case), run the dispatch
         * asynchronously. For headless tests, dispatch it immediately.
         */
        if (PlatformUI.isWorkbenchRunning())
        {
            PlatformUI.getWorkbench().getDisplay().asyncExec(dispatchResultUpdated);
        }
        else
        {
            dispatchResultUpdated.run();
        }
    }
}