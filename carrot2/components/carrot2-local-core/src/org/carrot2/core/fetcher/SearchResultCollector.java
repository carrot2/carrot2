
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

package org.carrot2.core.fetcher;


/**
 * A collector and counter of remaining running {@link ParallelFetcher}s.
 * 
 * @author Dawid Weiss
 */
final class SearchResultCollector
{
    private int fetchersCount;

    private SearchResult [] results;
    private SearchResult [] bad;

    /**
     * 
     */
    public SearchResultCollector(int releaseCount)
    {
        this.fetchersCount = releaseCount;
        this.results = new SearchResult [releaseCount];
    }

    /**
     * 
     */
    public void done(int fetcherIndex, SearchResult result)
    {
        synchronized (this)
        {
            results[fetcherIndex] = result;

            if (fetchersCount > 0)
            {
                fetchersCount--;
            }
            if (fetchersCount == 0)
            {
                this.notifyAll();
                splitErrorAndGood();
            }
        }
    }

    /**
     * 
     */
    public void blockUntilZero() throws InterruptedException
    {
        synchronized (this)
        {
            while (fetchersCount > 0)
            {
                this.wait();
            }
        }
    }

    /**
     *
     */
    private void splitErrorAndGood()
    {
        int errorCount = 0;
        for (int i = 0; i < results.length; i++)
        {
            if (results[i].error != null)
            {
                errorCount++;
            }
        }

        if (errorCount > 0)
        {
            final SearchResult [] good = new SearchResult [results.length - errorCount];
            bad = new SearchResult [errorCount];
            int ig = 0;
            int ib = 0;
            for (int i = 0; i < results.length; i++)
            {
                if (results[i].error != null)
                {
                    bad[ib] = results[i];
                    ib++;
                }
                else
                {
                    good[ig] = results[i];
                    ig++;
                }
            }
            results = good;
        }
    }

    /**
     * 
     */
    public SearchResult [] getNonErrorSearchResults()
    {
        return results;
    }

    /**
     * @return Returns <code>null</code> if there were no errors.
     */
    public SearchResult [] getErrorSearchResults()
    {
        return bad;
    }
}