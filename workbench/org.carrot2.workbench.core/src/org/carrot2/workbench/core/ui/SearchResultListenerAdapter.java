
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
 * Empty implementations of {@link ISearchResultListener} methods.
 */
public class SearchResultListenerAdapter implements ISearchResultListener
{
    @Override
    public void beforeProcessingResultUpdated()
    {
        // Empty
    }

    @Override
    public void processingResultUpdated(ProcessingResult result)
    {
        // Empty.
    }
    
    @Override
    public void afterProcessingResultUpdated()
    {
        // Empty
    }
}
