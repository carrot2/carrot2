package org.carrot2.source;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import carrot2.util.attribute.*;

/**
 * A superclass facilitating implementation of {@link DocumentSource}s wrapping
 * external search engines. This class implements helper methods for concurrent
 * querying of search services that limit the number of search results returned
 * in one request.
 */
@Bindable
public abstract class SearchEngine
    extends ProcessingComponentBase implements DocumentSource
{
    /**
     * Search mode defines how fetchers returned from {@link #createFetcher(SearchRange)}
     * are called.
     *
     * @label Search Mode
     * @see SearchMode
     */
    @Init
    @Input
    @Attribute(key="search-mode")
    private final SearchMode searchMode = SearchMode.SPECULATIVE;

    /**
     * Subclasses should override this method and return a {@link Callable}
     * instance that fetches search results in the given range.
     * <p>
     * Note the query (if any is required) should be passed at the concrete
     * class level. We are not concerned with it here.
     *
     * @param bucket The search range to fetch.
     */
    protected abstract Callable<SearchEngineResponse> createFetcher(final SearchRange bucket);

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
     * This method implements the logic of querying a typical search engine. If the
     * number of requested results is higher than the number of results on one response
     * page, then multiple (possibly concurrent) requests are issued via the provided
     * {@link ExecutorService}.
     */
    protected final SearchEngineResponse [] runQuery(final String query,
        final int start, final int results, final int maxResultIndex,
        final int resultsPerPage, final ExecutorService executor)
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
}