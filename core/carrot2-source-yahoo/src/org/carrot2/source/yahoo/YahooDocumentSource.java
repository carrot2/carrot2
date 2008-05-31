package org.carrot2.source.yahoo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Init;
import org.carrot2.source.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * A {@link DocumentSource} fetching {@link Document}s (search results) from Yahoo!.
 */
@Bindable
public final class YahooDocumentSource extends SearchEngine
{
    /** Logger for this class. */
    final static Logger logger = Logger.getLogger(YahooDocumentSource.class);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Static executor for running search threads.
     */
    private final static ExecutorService executor = SearchEngine.createExecutorService(
        MAX_CONCURRENT_THREADS, YahooDocumentSource.class);

    /**
     * The specific search service to be used by this document source. You can use this
     * attribute to choose which Yahoo! service to query, e.g. Yahoo Web Search or Yahoo
     * News.
     * 
     * @label Yahoo Search Service
     * @level Advanced
     */
    @Init
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        YahooWebSearchService.class, YahooNewsSearchService.class
    })
    public YahooSearchService service = new YahooWebSearchService();

    /**
     * Run a single query.
     */
    @Override
    public void process() throws ProcessingException
    {
        super.process(service.metadata, executor);
    }

    /**
     * Create a single page fetcher for the search range.
     */
    @Override
    protected final Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                return service.query(query, bucket.start, bucket.results);
            }
        };
    }
}
