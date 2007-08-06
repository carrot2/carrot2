
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

import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.RawDocument;

/**
 * A parallel fetcher executes multiple threads to download search 
 * results concurrently.
 *
 * @author Dawid Weiss
 */
public abstract class ParallelFetcher
{
    /**
     * Logger instance for this fetcher.
     */
    protected final Logger logger;

    /**
     * Start fetching results from this number (zero-based).
     */
    private final int startAtIndex;
    
    /**
     * Total number of results requested.
     */
    private final int resultsRequested;
    
    /**
     * Maximum index of a search result the underlying engine can fetch.
     * This is an absolute value, counting from result number 0.
     */
    private final int maximumResultIndex;

    /**
     * Number of search results available in a single fetch ("page").
     */
    private final int singleFetchSize;

    /**
     * Query issued to the underlying fetcher. 
     */
    private final String query;

    /**
     * @see #setParallelMode(boolean)
     */
    private boolean parallelMode;

    /**
     * @see #setIgnoreFetcherErrors(boolean)
     */
    private boolean ignoreFetcherErrors;
    
    /**
     * @param startAtIndex Start fetching results from this number (zero-based).
     * @param resultsRequested Total number of results requested.
     * @param maximumResultIndex Maximum index of a search result the underlying 
     * engine can fetch.
     * @param singleFetchSize Number of search results available in a single 
     * fetch ("page").
     */
    public ParallelFetcher(final String fetcherName, final String query, 
        int startAtIndex, int resultsRequested,
        int maximumResultIndex, int singleFetchSize)
    {
        this.logger = Logger.getLogger(ParallelFetcher.class + "." + fetcherName);

        this.startAtIndex = startAtIndex;
        this.resultsRequested = resultsRequested;

        this.singleFetchSize = singleFetchSize;
        this.maximumResultIndex = maximumResultIndex;

        this.query = query;
    }

    /**
     * <p>Controls parallelism in fetching. If <code>true</code>, 
     * a call to {@link #fetch()}, will start all fetch chunks immediately, 
     * without the initial check of how many results are actually available. If <code>
     * false</code>, the initial check is done against a search engine and then
     * (if there are more results than {@link #singleFetchSize} the remaining threads
     * are executed in parallel). 
     *
     * <p>Note that parallel mode of execution always starts multiple queries to the
     * search engine, so in case of pay-per-query engines, it is not advisable.
     */
    public void setParallelMode(boolean enableParallelMode)
    {
        this.parallelMode = enableParallelMode;
    }
    
    /**
     * If set to <code>true</code>, any error in the fetcher threads will
     * cause the entire fetcher to throw an exception. Note that
     * some search results may have already been 
     * passed to {@link #pushResults(int, RawDocument)}.
     * 
     * @param ignoreFetcherErrors
     */
    public void setIgnoreFetcherErrors(boolean ignoreFetcherErrors)
    {
        this.ignoreFetcherErrors = ignoreFetcherErrors;
    }

    /**
     * Starts fetching search results.
     *
     * @throws ProcessingException
     */
    public final void fetch() throws ProcessingException
    {
        if (startAtIndex < 0)
        {
            throw new ProcessingException("Start index must be greater than 0.");
        }

        if (resultsRequested <= 0)
        {
            throw new ProcessingException("Number of results must be positive.");
        }

        logger.info("Fetch: " + query + " (start: " + startAtIndex 
            + ", results requested: " + this.resultsRequested
            + ", " + (this.parallelMode ? "full parallel" : "with first request")
            + ")");

        // Adjust start and end window depending on search engine's limits.
        int startIndex = Math.min(startAtIndex, maximumResultIndex);
        int endIndex = Math.min(startAtIndex + resultsRequested, maximumResultIndex);

        if (logger.isDebugEnabled())
        {
            logger.debug("Initial fetch window: " + startIndex + "--" + endIndex 
                + " (" + this.singleFetchSize + "/page)");
        }

        int resultsNeeded = endIndex - startIndex;
        if (resultsNeeded == 0)
        {
            // No results to fetch.
            return;
        }

        final long startTime = System.currentTimeMillis();
        if (!parallelMode) {
            // Perform the inital fetch to establish the number of search results.
            final SingleFetcher initialFetcher = getFetcher();
            final SearchResult initialResult;
            try {
                final int fetchSize = Math.min(singleFetchSize, resultsNeeded);
                initialResult = initialFetcher.fetch(query, startIndex, fetchSize);

                if (initialResult.error != null)
                {
                    throw initialResult.error;
                }
            } catch (ProcessingException e) {
                throw e;
            } catch (Throwable t) {
                throw new ProcessingException(t);
            }

            // Modify count of needed results based on the response 
            // to the initial request.
            logger.debug("Estimated number of results: " + initialResult.totalEstimated);

            final int totalEstimated = (int) Math.min(Integer.MAX_VALUE, 
                initialResult.totalEstimated);

            if (totalEstimated < maximumResultIndex)
            {
                startIndex = Math.min(startAtIndex, totalEstimated);
                endIndex = Math.min(startAtIndex + resultsRequested, totalEstimated);

                logger.debug("Adjusted fetch window: " + startIndex + "--" + endIndex 
                    + " (" + this.singleFetchSize + "/page)");

                resultsNeeded = endIndex - startIndex;
                if (resultsNeeded == 0)
                {
                    // No results to fetch.
                    return;
                }
            }

            resultsNeeded = pushResults0(resultsNeeded, startIndex, initialResult, new HashSet());
            startIndex += singleFetchSize;
            
            if (initialResult.results.length == 0)
            {
                logger.warn("Initial fetch returned no results.");
                return;
            }
        }

        if (resultsNeeded <= 0)
        {
            return;
        }

        // Calculate the number of buckets and initialize parallel fetcher threads.
        final int buckets = resultsNeeded / singleFetchSize 
            + (resultsNeeded % singleFetchSize == 0 ? 0 : 1);
        final SearchResultCollector collector = new SearchResultCollector(buckets);
        final FetcherThread [] fetcherThreads = new FetcherThread [buckets];

        for (int i = 0; i < buckets; i++)
        {
            final SingleFetcher fetcher = getFetcher();
            final int fetchSize = Math.min(singleFetchSize, endIndex - startIndex);
            fetcherThreads[i] = new FetcherThread(logger, collector, i, 
                fetcher, query, startIndex, fetchSize);
            fetcherThreads[i].start();

            startIndex += singleFetchSize;
        }

        try
        {
            collector.blockUntilZero();

            final SearchResult [] bad = collector.getErrorSearchResults();
            if (!ignoreFetcherErrors && bad != null && bad.length > 0)
            {
                throw new ProcessingException("Search results fetching failed in "
                    + bad.length + " fetchers: " + bad[0].error);
            }

            /**
             * Use a hash set to detect which URLs have been pushed already. Note
             * that we don't need any synchronization here, as we've already
             * collected output from all threads.
             */
            final HashSet urlsPushed = new HashSet();

            final SearchResult [] results = collector.getNonErrorSearchResults();
            for (int i = 0; i < results.length; i++)
            {
                resultsNeeded = pushResults0(resultsNeeded, results[i].at, results[i], urlsPushed);
            }
        }
        catch (InterruptedException e)
        {
            throw new ProcessingException("Search took too long (interrupted).");
        }

        final long endTime = System.currentTimeMillis();
        logger.info("Finished [" + (endTime - startTime) + "ms.]: " + query);
    }

    /**
     * This method should return a new fetcher when requested 
     * (object capable of fetching results from the search engine).
     */
    protected abstract SingleFetcher getFetcher();

    /**
     * This method is called from {@link #pushResults0(int, int, SearchResult, HashSet)}
     * and passes all search results fetched in increasing order of their index.
     * 
     * @param at Index of the search result.
     * @param rawDocument {@link RawDocument} with search result's data.
     */
    protected abstract void pushResults(int at, final RawDocument rawDocument) throws ProcessingException;

    /**
     * Method used internally to collect search results.
     * 
     * @param count Number of results still needed.
     * @param at Starting index of the first search result in the results block.
     */
    private int pushResults0(int count, int at, SearchResult result, HashSet urlsPushed) throws ProcessingException
    {
        if (result.error != null)
        {
            throw new ProcessingException(result.error);
        }

        final RawDocument [] rdocs = result.results;
        for (int i = 0; count > 0 && i < rdocs.length; i++, count--)
        {
            final RawDocument rawDocument = rdocs[i];
            if (!urlsPushed.contains(rawDocument.getUrl()))
            {
                urlsPushed.add(rawDocument.getUrl());
                pushResults(at + i, rawDocument);
            }
        }

        return count;
    }
}
