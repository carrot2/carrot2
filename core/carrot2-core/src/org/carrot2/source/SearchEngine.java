package org.carrot2.source;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.attribute.constraint.NotBlank;

import com.google.common.base.Predicate;

/**
 * A superclass facilitating implementation of {@link DocumentSource}s wrapping external
 * search engines. This class implements helper methods for concurrent querying of search
 * services that limit the number of search results returned in one request.
 */
@Bindable
public abstract class SearchEngine extends ProcessingComponentBase implements
    DocumentSource
{
    /**
     * Search mode defines how fetchers returned from {@link #createFetcher(SearchRange)}
     * are called.
     * 
     * @label Search Mode
     * @level Advanced
     * @see SearchMode
     */
    @Processing
    @Input
    @Attribute(key = "search-mode")
    public SearchMode searchMode = SearchMode.SPECULATIVE;

    /**
     * Starting index of the first result to fetch.
     * 
     * @label Start index
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.START)
    public int start = 0;

    /**
     * Number of results to fetch.
     * 
     * @label Results count
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @IntRange(min = 10)
    public int results = 100;

    /**
     * Search query to execute.
     * 
     * @label Query
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.QUERY)
    @Required
    @NotBlank
    public String query;

    /**
     * Number of total matching documents. This may be an approximation.
     * 
     * @label Results Total
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL)
    public long resultsTotal;

    /**
     * A collection of documents retrieved for the query.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    public Collection<Document> documents;

    /**
     * This component usage statistics.
     */
    public SearchEngineStats statistics = new SearchEngineStats();

    /**
     * Indicates whether the search engine returned a compressed result stream.
     */
    @Processing
    @Output
    @Attribute
    public boolean compressed;

    /**
     * Run a request the search engine's API, setting <code>documents</code> to the set of
     * returned documents.
     */
    protected void process(SearchEngineMetadata metadata, ExecutorService executor)
        throws ProcessingException
    {
        final SearchEngineResponse [] responses = runQuery(query, start, results,
            metadata, executor);

        compressed = false;
        if (responses.length > 0)
        {
            // Collect documents from the responses.
            documents = new ArrayList<Document>(Math
                .min(results, metadata.maxResultIndex));
            collectDocuments(documents, responses);

            // Filter out duplicated URLs.
            final Iterator<Document> i = documents.iterator();
            final Predicate<Document> p = new UniqueFieldPredicate(Document.CONTENT_URL);
            while (i.hasNext())
            {
                if (!p.apply(i.next()))
                {
                    i.remove();
                }
            }

            resultsTotal = responses[0].getResultsTotal();

            for (int j = 0; j < responses.length; j++)
            {
                final String compression = (String) responses[j].metadata
                    .get(SearchEngineResponse.COMPRESSION_KEY);
                if (compression != null && "gzip".contains(compression))
                {
                    compressed = true;
                }
            }
        }
        else
        {
            documents = Collections.<Document> emptyList();
            resultsTotal = 0;
        }
    }

    /**
     * Subclasses should override this method and return a {@link Callable} instance that
     * fetches search results in the given range.
     * <p>
     * Note the query (if any is required) should be passed at the concrete class level.
     * We are not concerned with it here.
     * 
     * @param bucket The search range to fetch.
     */
    protected abstract Callable<SearchEngineResponse> createFetcher(
        final SearchRange bucket);

    /**
     * Collects documents from an array of search engine's responses.
     */
    protected final void collectDocuments(Collection<Document> collector,
        SearchEngineResponse [] responses)
    {
        for (final SearchEngineResponse response : responses)
        {
            collector.addAll(response.results);
        }
    }

    /**
     * This method implements the logic of querying a typical search engine. If the number
     * of requested results is higher than the number of results on one response page,
     * then multiple (possibly concurrent) requests are issued via the provided
     * {@link ExecutorService}.
     */
    protected final SearchEngineResponse [] runQuery(final String query, final int start,
        final int results, SearchEngineMetadata metadata, final ExecutorService executor)
        throws ProcessingException
    {
        this.statistics.incrQueryCount();

        // Split the requested range into pages.
        SearchRange [] buckets = SearchRange.getSearchRanges(start, results,
            metadata.maxResultIndex, metadata.resultsPerPage, metadata.incrementByPage);

        // Check preconditions.
        if (query == null || query.trim().equals("") || buckets.length == 0)
        {
            return new SearchEngineResponse [0];
        }

        try
        {
            // Initialize output documents array.
            final ArrayList<SearchEngineResponse> responses = new ArrayList<SearchEngineResponse>(
                buckets.length);

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
                        buckets = SearchRange.getSearchRanges(buckets[0].results,
                            (int) resultsTotal, metadata.maxResultIndex,
                            metadata.resultsPerPage, metadata.incrementByPage);
                    }
                }
            }

            // Run concurrent requests using the executor.
            final ArrayList<Callable<SearchEngineResponse>> fetchers = new ArrayList<Callable<SearchEngineResponse>>(
                buckets.length);

            for (final SearchRange r : buckets)
            {
                fetchers.add(createFetcher(r));
            }

            // Run requests in parallel.
            final List<Future<SearchEngineResponse>> futures = executor
                .invokeAll(fetchers);

            // Collect results.
            for (final Future<SearchEngineResponse> future : futures)
            {
                if (!future.isCancelled())
                {
                    responses.add(future.get());
                }
            }

            return responses.toArray(new SearchEngineResponse [responses.size()]);
        }
        catch (final IOException e)
        {
            throw new ProcessingException(e.getMessage(), e);
        }
        catch (final InterruptedException e)
        {
            // If interrupted, return with no error.
            return new SearchEngineResponse [0];
        }
        catch (final Exception e)
        {
            Throwable cause = e.getCause();
            if (cause == null)
            {
                cause = e;
            }

            throw new ProcessingException(cause.getMessage(), e);
        }
    }

    /**
     * @return Return a new {@link ThreadFactory} that sets context class loader for newly
     *         created threads.
     */
    protected static ThreadFactory contextClassLoaderThreadFactory(
        final ClassLoader clazzLoader)
    {
        final ThreadFactory tf = new ThreadFactory()
        {
            private final ThreadFactory delegate = Executors.defaultThreadFactory();

            public Thread newThread(Runnable r)
            {
                final Thread t = delegate.newThread(r);
                t.setDaemon(true);
                t.setContextClassLoader(clazzLoader);
                return t;
            }
        };

        return tf;
    }

    /**
     * @return Return an executor service with a fixed thread pool of
     *         <code>maxConcurrentThreads</code> threads and context class loader
     *         initialized to <code>clazz</code>'s context class loader.
     */
    protected static ExecutorService createExecutorService(int maxConcurrentThreads,
        Class<?> clazz)
    {
        return Executors.newFixedThreadPool(maxConcurrentThreads,
            contextClassLoaderThreadFactory(clazz.getClassLoader()));
    }

    /**
     * An implementation of {@link Callable} that increments page request count statistics
     * before the actual search is made.
     */
    protected abstract class SearchEngineResponseCallable implements
        Callable<SearchEngineResponse>
    {
        public final SearchEngineResponse call() throws Exception
        {
            statistics.incrPageRequestCount();
            return search();
        }

        /**
         * Performs the actual search and returns the response.
         */
        public abstract SearchEngineResponse search() throws Exception;
    }
}