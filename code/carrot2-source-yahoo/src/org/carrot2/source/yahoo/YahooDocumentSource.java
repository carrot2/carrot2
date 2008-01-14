package org.carrot2.source.yahoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.carrot2.core.Document;
import org.carrot2.core.DocumentSource;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.parameter.Attribute;
import org.carrot2.core.parameter.Bindable;
import org.carrot2.core.parameter.BindingDirection;
import org.carrot2.core.parameter.BindingPolicy;
import org.carrot2.core.parameter.Parameter;
import org.carrot2.source.SearchMode;
import org.carrot2.source.SearchRange;

@Bindable
public final class YahooDocumentSource 
    extends ProcessingComponentBase implements DocumentSource
{
    /** Logger for this class. */
    private final static Logger logger = Logger.getLogger(YahooDocumentSource.class);

    /**
     * Static executor for running search threads to Yahoo!. You can set the
     * number of concurrent requests from <b>all</b> instances of this component
     * here.
     */
    private final static ExecutorService executor = Executors.newFixedThreadPool(/* max threads */ 10);

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    private YahooService searchService = new YahooService();  

    @Parameter(key="search-mode", policy=BindingPolicy.INSTANTIATION)
    private SearchMode searchMode = SearchMode.SPECULATIVE;

    @Parameter(key="start", policy=BindingPolicy.RUNTIME)
    private int start = 0;

    @Parameter(key="results", policy=BindingPolicy.RUNTIME)
    private int results = 100;

    @Parameter(key="query", policy=BindingPolicy.RUNTIME)
    private String query;

    @SuppressWarnings("unused")
    @Attribute(key="results-total", bindingDirection = BindingDirection.OUT)
    private long resultsTotal;

    @SuppressWarnings("unused")
    @Attribute(key="documents", bindingDirection = BindingDirection.OUT)
    private Collection<Document> documents = Collections.<Document> emptyList();

    /**
     * Run a request against Yahoo API and set {@link #documents} to 
     * the set of returned documents.
     */
    @Override
    public void performProcessing() throws ProcessingException
    {
        final YahooServiceParams params = searchService.serviceParams;
        
        final SearchEngineResponse [] responses = runQuery(
            query, start, results, params.maxResultIndex, params.resultsPerPage,
            searchMode, executor);

        if (responses.length > 0)
        {
            // Collect documents from the responses.
            documents.clear();
            collectDocuments(documents, responses);

            resultsTotal = responses[0].getResultsTotal();
        }
        else
        {
            documents = Collections.<Document> emptyList();
            resultsTotal = 0;
        }
    }

    /** 
     * Collects documents from an array of search engine's responses.
     */
    protected final void collectDocuments(
        Collection<Document> collector, SearchEngineResponse [] responses)
    {
        for (SearchEngineResponse response : responses)
        {
            collector.addAll(response.results);
        }
    }

    /**
     * This method implements the logic of querying a typical search engine. If the
     * number of requested results is higher than the number of results on one response
     * page, then concurrent requests are issued.
     */
    protected final SearchEngineResponse [] runQuery(
        final String query,
        final int start, final int results,
        final int maxResultIndex, final int resultsPerPage,
        final SearchMode searchMode,
        final ExecutorService executor)
        throws ProcessingException
    {
        // Split the requested range into pages.
        SearchRange [] buckets = 
            SearchRange.getSearchRanges(start, results, maxResultIndex, resultsPerPage);

        // Check preconditions.
        if (query == null || query.trim().equals("") || buckets.length == 0)
        {
            return new SearchEngineResponse [0];
        }

        try {
            if (logger.isDebugEnabled())
            {
                logger.info("Results: " + results + ", query: " + query);
            }

            // Initialize output documents array.
            final ArrayList<SearchEngineResponse> responses = 
                new ArrayList<SearchEngineResponse>(buckets.length);

            // If in conservative mode, run the first request to estimate the
            // number of needed results.
            if (buckets.length == 1 || searchMode == SearchMode.CONSERVATIVE)
            {
                final SearchEngineResponse response = createFetcher(buckets[0]).call();

                final long resultsTotal = response.getResultsTotal();
                responses.add(response);

                if (buckets.length == 1)
                {
                    // If there was just one bucket, there is no need to go further on.
                    return responses.toArray(new SearchEngineResponse [responses.size()]);
                }
                else
                {
                    // We do have an estimate of results now, modify it
                    // and recalculate the buckets.
                    if (resultsTotal != -1 && resultsTotal < results)
                    {
                        buckets = SearchRange.getSearchRanges(
                            buckets[0].results, (int) resultsTotal, maxResultIndex, resultsPerPage);
                    }
                }
            }

            // Run concurrent requests using the executor. 
            final ArrayList<Callable<SearchEngineResponse>> fetchers = 
                new ArrayList<Callable<SearchEngineResponse>>(buckets.length);

            for (final SearchRange r : buckets)
            {
                fetchers.add(createFetcher(r));
            }

            // Run requests in parallel.
            final List<Future<SearchEngineResponse>> futures = executor.invokeAll(fetchers);

            // Collect results.
            for (Future<SearchEngineResponse> future : futures)
            {
                if (!future.isCancelled())
                {
                    responses.add(future.get());
                }
            }

            return responses.toArray(new SearchEngineResponse [responses.size()]);
        }
        catch (IOException e)
        {
            throw new ProcessingException(e.getMessage(), e);
        }
        catch (InterruptedException e)
        {
            // If interrupted, return with no error.
            return new SearchEngineResponse [0];
        }
        catch (Exception e)
        {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;

            throw new ProcessingException(cause.getMessage(), e);
        }
    }

    /**
     * Create a single page fetcher for the search range.
     */
    protected final Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new Callable<SearchEngineResponse>()
        {
            @Override
            public SearchEngineResponse call() throws Exception
            {
                return searchService.query(query, bucket.start, bucket.results);
            }
        };
    }
}
