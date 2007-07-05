
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.fetcher;

import org.carrot2.core.ProcessingException;

/**
 * A fetcher downloads search results for a given query.
 * 
 * @author Dawid Weiss
 */
public abstract class SingleFetcher
{
    /**
     * 
     */
    public abstract SearchResult fetch(String query, int startAt) throws ProcessingException;
}
