package org.carrot2.source.yahoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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

        // Split the requested range into pages.
        SearchRange [] buckets = 
            SearchRange.getSearchRanges(start, results, params.maxResultIndex, params.resultsPerPage);

        // Check preconditions.
        if (query == null || query.trim().equals("") || buckets.length == 0)
        {
            documents = Collections.<Document> emptyList();
            return;
        }

        try {
            // Initialize output documents array.
            documents = new ArrayList<Document>(Math.min(results, params.maxResultIndex));

            // If in conservative mode, run the first request to estimate the
            // number of needed results.
            if (buckets.length == 1 || searchMode == SearchMode.CONSERVATIVE)
            {
                final SearchResponse response = searchService.query(
                    query, buckets[0].start, buckets[0].results);
                
                documents.addAll(response.results);
                resultsTotal = response.resultsTotal;

                if (buckets.length == 1)
                {
                    // If there was just one bucket, there is no need
                    // to go further on.
                    return;
                }
                else
                {
                    // We do have an estimate of results now, modify it
                    // and recalculate the buckets.
                    if (resultsTotal < results)
                    {
                        buckets = SearchRange.getSearchRanges(
                            buckets[0].results, (int) resultsTotal, params.maxResultIndex, params.resultsPerPage);
                    }
                }
            }

            // Run concurrent requests using the executor. 
            final ArrayList<Callable<SearchResponse>> fetchers = 
                new ArrayList<Callable<SearchResponse>>();
    
            for (final SearchRange r : buckets) {
                fetchers.add(new Callable<SearchResponse>()
                {
                    @Override
                    public SearchResponse call() throws Exception
                    {
                        return searchService.query(query, r.start, r.results);
                    }
                });
            }

            // Run requests in parallel.
            final List<Future<SearchResponse>> responses = executor.invokeAll(fetchers);

            // Collect results.
            for (Future<SearchResponse> response : responses)
            {
                if (!response.isCancelled())
                {
                    this.documents.addAll(response.get().results);
                }
            }

            // Fill in informational attributes, if not done previously. 
            if (responses.size() > 0 && searchMode != SearchMode.CONSERVATIVE) {
                final SearchResponse response = responses.get(0).get();
                this.resultsTotal = response.resultsTotal;
            }
        }
        catch (IOException e)
        {
            throw new ProcessingException(e.getMessage(), e);
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;

            throw new ProcessingException(cause.getMessage(), e);
        }
        catch (InterruptedException e)
        {
            // If interrupted, just fall through.
        }
    }
}
