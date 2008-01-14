package org.carrot2.source.yahoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.parameter.Attribute;
import org.carrot2.core.parameter.Bindable;
import org.carrot2.core.parameter.BindingDirection;
import org.carrot2.core.parameter.BindingPolicy;
import org.carrot2.core.parameter.Parameter;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SearchRange;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

@Bindable
public final class YahooDocumentSource extends SearchEngine
{
    /** Logger for this class. */
    final static Logger logger = Logger.getLogger(YahooDocumentSource.class);

    /**
     * Static executor for running search threads to Yahoo!. You can set the
     * number of concurrent requests from <b>all</b> instances of this component
     * here.
     */
    private final static ExecutorService executor = Executors.newFixedThreadPool(/* max threads */ 10);

    @Parameter(policy = BindingPolicy.INSTANTIATION)
    private YahooService searchService = new YahooService();  

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
    private Collection<Document> documents;

    /**
     * Run a request against Yahoo API and set {@link #documents} to 
     * the set of returned documents.
     */
    @Override
    public void performProcessing() throws ProcessingException
    {
        final YahooServiceParams params = searchService.serviceParams;
        
        final SearchEngineResponse [] responses = runQuery(
            query, start, results, params.maxResultIndex, params.resultsPerPage, executor);

        if (responses.length > 0)
        {
            // Collect documents from the responses.
            documents = new ArrayList<Document>(Math.min(results, params.maxResultIndex));
            collectDocuments(documents, responses);

            // Filter out duplicated URLs (may happen, the results are paged based
            // on a heuristic at Yahoo).
            final Iterator<Document> i = documents.iterator();
            final Predicate<Document> p = new UniqueFieldPredicate(Document.CONTENT_URL);
            while (i.hasNext())
            {
                if (!p.apply(i.next())) i.remove();
            }

            resultsTotal = responses[0].getResultsTotal();
        }
        else
        {
            documents = Collections.<Document> emptyList();
            resultsTotal = 0;
        }
    }

    /**
     * Create a single page fetcher for the search range.
     */
    @Override
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
