
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

import org.apache.log4j.Logger;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.RawDocument;

/**
 * A parallel fetcher can spawn multiple threads to download search results.
 * 
 * @author Dawid Weiss
 */
public abstract class ParallelFetcher
{
    /**
     * Logger instance for this fetcher.
     */
    protected final Logger logger;

    private final int startAt;
    private final int resultsRequested;
    private final int maximumResults;
    private final String query;

    /**
     * Single fetch size if in full parallel mode.
     */
    private int fetchSize;

    /**
     * 
     */
    public ParallelFetcher(String fetcherName, String query, int startAt, int resultsRequested, int maximumResults)
    {
        this.logger = Logger.getLogger(ParallelFetcher.class + "." + fetcherName);
        this.startAt = startAt;
        this.resultsRequested = resultsRequested;
        this.maximumResults = maximumResults;
        this.query = query;
    }

    /**
     * <p>Enables full parallelism in fetching. Upon a call to {@link #fetch()}, all 
     * fetch chunks will be started immediately, without the initial check of how
     * many results are actually available.
     * 
     * <p>Note that this mode of execution always starts multiple queries to the
     * search engine, so in case of pay-per-query engines, it is not advisable.
     */
    public void setFullParallelMode(int singleFetchSize)
    {
        if (singleFetchSize <= 0) {
            throw new IllegalArgumentException();
        }

        this.fetchSize = singleFetchSize;
    }
    
    /**
     * Starts fetching search results.
     * 
     * @throws ProcessingException
     */
    public final void fetch() throws ProcessingException
    {
        if (startAt < 0) {
            throw new ProcessingException("Start index must be greater than 0.");
        }

        // Adjust start and end.
        int count = Math.min(resultsRequested, maximumResults);
        int start = Math.max(0, Math.min(startAt, maximumResults - count));

        if (count == 0)
        {
            return;
        }

        final long startTime = System.currentTimeMillis();
        final int currentFetchSize;

        if (this.fetchSize == 0) {
            // Normal mode (inital fetch followed by parallel threads).
            logger.info("Initial fetch: " + query);

            final SingleFetcher initialFetcher = getFetcher();
            final SearchResult initialResult;
            try {
                initialResult = initialFetcher.fetch(query, start);
                logger.debug("Fetcher retrieved: " + initialResult);
            } catch (ProcessingException e) {
                throw e;
            } catch (Throwable t) {
                throw new ProcessingException(t);
            }

            // Modify count of needed results based on availability
            // and start parallel fetchers.
            logger.info("Initial result retrieved: " + initialResult);
            count = Math.min((int) Math.min(Integer.MAX_VALUE, initialResult.totalEstimated), count);
            count = pushResults0(count, 0, initialResult);
            
            // Estimate fetch size and advance start point.
            currentFetchSize = initialResult.results.length;
            start += currentFetchSize;
        } else {
            // full parallel mode. Fetch size is given in advance.
            currentFetchSize = this.fetchSize;
        }

        if (count <= 0 || currentFetchSize == 0)
        {
            // Nothing more.
            return;
        }

        final int buckets = count / currentFetchSize + (count % currentFetchSize == 0 ? 0 : 1);
        final SearchResultCollector collector = new SearchResultCollector(buckets);
        final FetcherThread [] fetcherThreads = new FetcherThread [buckets];

        for (int i = 0; i < buckets; i++)
        {
            final SingleFetcher fetcher = getFetcher();
            fetcherThreads[i] = new FetcherThread(logger, collector, i, fetcher, query, start);
            fetcherThreads[i].start();

            start += currentFetchSize;
        }

        try
        {
            collector.blockUntilZero();

            final SearchResult [] results = collector.getSearchResults();
            for (int i = 0; i < results.length; i++)
            {
                count = pushResults0(count, results[i].at, results[i]);
            }
        }
        catch (InterruptedException e)
        {
            throw new ProcessingException("Search took too long.");
        }

        final long endTime = System.currentTimeMillis();
        logger.info("Finished [" + (endTime - startTime) + "ms.]: " + query);
    }

    /**
     * 
     */
    public abstract SingleFetcher getFetcher();

    /**
     * 
     */
    public abstract void pushResults(int at, final RawDocument rawDocument) throws ProcessingException;

    /**
     * 
     */
    private int pushResults0(int count, int at, SearchResult result) throws ProcessingException
    {
        if (result.error != null)
        {
            throw new ProcessingException(result.error);
        }

        final RawDocument [] rdocs = result.results;
        for (int i = 0; count > 0 && i < rdocs.length; i++, count--)
        {
            pushResults(at + i, rdocs[i]);
        }

        return count;
    }
}
