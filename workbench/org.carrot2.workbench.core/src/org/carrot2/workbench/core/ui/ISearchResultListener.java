package org.carrot2.workbench.core.ui;

import org.carrot2.core.ProcessingResult;

/**
 * Callback event listener associated with {@link SearchResult}.
 * <p>
 * All methods are guaranteed to be invoked from the SWT thread, but keep processing swift
 * (or schedule another runnable on the GUI queue) to keep the GUI responsive.
 */
public interface ISearchResultListener
{
    /**
     * New processing result is available.
     */
    void processingResultUpdated(ProcessingResult result);
}
