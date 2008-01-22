package org.carrot2.source.yahoo;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.parameter.*;
import org.carrot2.source.*;

import com.google.common.base.Predicate;

/**
 * A {@link DocumentSource} fetching {@link Document}s (search results)
 * from Yahoo!.
 */
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

    @Init
    @Input
    @Parameter
    private YahooSearchService service = new YahooWebSearchService();  

    @Attribute(key=AttributeNames.START, bindingDirection = BindingDirection.IN)
    private int start = 0;

    @Attribute(key=AttributeNames.RESULTS, bindingDirection = BindingDirection.IN)
    private int results = 100;

    @Attribute(key=AttributeNames.QUERY, bindingDirection = BindingDirection.IN)
    private String query;

    @SuppressWarnings("unused")
    @Attribute(key=AttributeNames.RESULTS_TOTAL, bindingDirection = BindingDirection.OUT)
    private long resultsTotal;

    @SuppressWarnings("unused")
    @Attribute(key=AttributeNames.DOCUMENTS, bindingDirection = BindingDirection.OUT)
    private Collection<Document> documents;

    /**
     * Run a request against Yahoo API and set {@link #documents} to 
     * the set of returned documents.
     */
    @Override
    public void process() throws ProcessingException
    {
        final SearchEngineResponse [] responses = runQuery(
            query, start, results, service.maxResultIndex, service.resultsPerPage, executor);

        if (responses.length > 0)
        {
            // Collect documents from the responses.
            documents = new ArrayList<Document>(Math.min(results, service.maxResultIndex));
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
                return service.query(query, bucket.start, bucket.results);
            }
        };
    }
}
