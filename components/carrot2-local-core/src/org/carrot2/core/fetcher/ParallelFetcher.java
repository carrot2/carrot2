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

        // Start an initial query
        final long startTime = System.currentTimeMillis();
        logger.info("Initial fetch: " + query);

        final SingleFetcher initialFetcher = getFetcher();
        final SearchResult initialResult = initialFetcher.fetch(query, start);

        // Modify count of needed results based on availability
        // and start parallel fetchers.
        logger.info("Initial result retrieved: " + initialResult);
        count = Math.min(initialResult.totalEstimated, count);
        count = pushResults0(count, 0, initialResult);

        if (count <= 0)
        {
            // Nothing more.
            return;
        }

        final int EXPECTED_RESULTS_PER_KEY = initialResult.results.length;
        final int buckets = count / EXPECTED_RESULTS_PER_KEY + (count % EXPECTED_RESULTS_PER_KEY == 0 ? 0 : 1);
        final SearchResultCollector collector = new SearchResultCollector(buckets);
        final FetcherThread [] fetcherThreads = new FetcherThread [buckets];

        for (int i = 0; i < buckets; i++)
        {
            start += EXPECTED_RESULTS_PER_KEY;

            final SingleFetcher fetcher = getFetcher();
            fetcherThreads[i] = new FetcherThread(logger, collector, i, fetcher, query, start);
            fetcherThreads[i].start();
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
