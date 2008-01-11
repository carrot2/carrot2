package org.carrot2.source.yahoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.carrot2.core.Document;
import org.carrot2.core.DocumentSource;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.parameter.Attribute;
import org.carrot2.core.parameter.Bindable;
import org.carrot2.core.parameter.BindingDirection;
import org.carrot2.core.parameter.BindingPolicy;
import org.carrot2.core.parameter.Parameter;
import org.carrot2.source.SearchRange;

@Bindable
public class YahooDocumentSource 
    extends ProcessingComponentBase 
    implements DocumentSource
{
    /**
     * Static executor for threads running Yahoo! API queries.
     */
    private static final ExecutorService executor = Executors.newFixedThreadPool(/* max threads */ 10);

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    private int maxResultIndex = 1000;

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    private int resultsPerPage = 50;

    @Attribute(bindingDirection = BindingDirection.IN)
    private int start = 0;

    @Attribute(bindingDirection = BindingDirection.IN)
    private int results = 100;

    @Attribute(bindingDirection = BindingDirection.IN)
    private String query;

    @Attribute(bindingDirection = BindingDirection.OUT)
    private long resultsTotal;

    @Attribute(bindingDirection = BindingDirection.OUT)
    private Collection<Document> documents = Collections.<Document> emptyList();

    /**
     * Run a request against Yahoo API and set {@link #documents} to 
     * the set of returned documents.
     */
    @Override
    public void performProcessing() throws ProcessingException
    {
        // Split the requested range into pages.
        final SearchRange [] buckets = 
            SearchRange.getSearchRanges(start, results, maxResultIndex, resultsPerPage);

        // Check preconditions.
        if (query == null || query.trim().equals("") || buckets.length == 0)
        {
            documents = Collections.<Document> emptyList();
            return;
        }

        // Run concurrent requests using the executor. 
        final ArrayList<PageFetcher> bucketFetchers = new ArrayList<PageFetcher>();
        for (SearchRange r : buckets) {
            bucketFetchers.add(new PageFetcher(r));
        }

        try
        {
            // Run the first request to estimate the result set size.
            final Future<SearchResults> firstResult = executor.submit(bucketFetchers.remove(0));
            final List<Future<SearchResults>> results = executor.invokeAll(bucketFetchers);
        }
        catch (InterruptedException e)
        {
            // If interrupted, just fall through.
        }
    }
}
