
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
     * Invoked before all the handlers processed {@link #processingResultUpdated}.
     */
    void beforeProcessingResultUpdated();

    /**
     * New processing result is available.
     */
    void processingResultUpdated(ProcessingResult result);
    
    /**
     * Invoked after all the handlers processed {@link #processingResultUpdated}.
     */
    void afterProcessingResultUpdated();
}
