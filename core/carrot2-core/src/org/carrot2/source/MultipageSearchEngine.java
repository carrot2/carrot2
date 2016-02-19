
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

import org.carrot2.shaded.guava.common.base.Predicate;

/**
 * A base class facilitating implementation of {@link IDocumentSource}s wrapping external
 * search engines with remote/ network-based interfaces. This class implements helper
 * methods for concurrent querying of search services that limit the number of search
 * results returned in one request.
 * 
 * @see SimpleSearchEngine
 */
@Bindable
public abstract class MultipageSearchEngine extends SearchEngineBase
{
    /**
     * Search mode defines how fetchers returned from {@link #createFetcher}
     * are called.
     * @see SearchMode
     */
    @Processing
    @Input
    @Attribute(key = "search-mode")
    @Level(AttributeLevel.ADVANCED)
    @Label("Search Mode")
    @Group(DefaultGroups.SOURCE_PAGING)
    public SearchMode searchMode = SearchMode.SPECULATIVE;

    /**
     * Run a request the search engine's API, setting <code>documents</code> to the set of
     * returned documents.
     */
    protected void process(MultipageSearchEngineMetadata metadata,
        ExecutorService executor) throws ProcessingException
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
        final int results, MultipageSearchEngineMetadata metadata,
        final ExecutorService executor) throws ProcessingException
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
     * An implementation of {@link Callable} that increments page request count statistics
     * before the actual search is made.
     */
    protected abstract class SearchEngineResponseCallable implements
        Callable<SearchEngineResponse>
    {
        public final SearchEngineResponse call() throws Exception
        {
            statistics.incrPageRequestCount();
            final SearchEngineResponse response = search();
            afterFetch(response);
            return response;
        }

        /**
         * Performs the actual search and returns the response.
         */
        public abstract SearchEngineResponse search() throws Exception;
    }

    /**
     * A single result window to fetch.
     */
    protected final static class SearchRange
    {
        /** Empty range. */
        private static final SearchRange [] EMPTY_RANGE = new SearchRange [0];

        /** Start index from which to search (inclusive). */
        public final int start;

        /** How many results to fetch. */
        public final int results;

        /**
         * Create a new search range.
         * 
         * @param start Start index of the first result to return (0-based).
         * @param results The number of results to return. The actual number of results
         *            returned by a search service may be lower than this number.
         */
        public SearchRange(int start, int results)
        {
            this.start = start;
            this.results = results;
        }

        /**
         * Given an unconstrained start and results count, adjust it to the allowed window
         * and split into page buckets if necessary.
         */
        public static SearchRange [] getSearchRanges(int start, int results,
            int maxIndex, int resultsPerPage, boolean incrementByPage)
        {
            // Sanity check.
            results = Math.max(results, 0);
            start = Math.max(start, 0);

            int startIndex = Math.min(start * (incrementByPage ? resultsPerPage : 1),
                maxIndex);
            final int endIndex = Math.min(start * (incrementByPage ? resultsPerPage : 1)
                + results, maxIndex);

            final int resultsNeeded = endIndex - startIndex;
            if (resultsNeeded == 0)
            {
                return EMPTY_RANGE;
            }

            final int lastBucketSize = resultsNeeded % resultsPerPage;
            final int bucketsNeeded = resultsNeeded / resultsPerPage
                + (lastBucketSize > 0 ? 1 : 0);

            final SearchRange [] buckets = new SearchRange [bucketsNeeded];
            for (int i = 0; i < buckets.length; i++)
            {
                final int window = Math.min(resultsPerPage, endIndex - startIndex);
                buckets[i] = new SearchRange((incrementByPage ? start + i : startIndex),
                    window);
                startIndex += window;
            }

            return buckets;
        }
    }

    /**
     * Search mode for data source components that implement parallel request to some
     * search service.
     */
    public enum SearchMode
    {
        /**
         * In this mode, an initial search request is performed to estimate the number of
         * documents available on the server. Then the requested number of documents is
         * adjusted according to the number of documents available to minimize the number
         * of requests.
         */
        CONSERVATIVE,

        /**
         * In this mode, the number of requested documents is divided by the maximum
         * number of documents the search engine can return in a single request. The
         * result is the number of <b>concurrent</b> requests launched to the search
         * service.
         * <p>
         * Note that speculative threads cause larger load on the search service and will
         * exhaust your request pool quicker (if it is limited).
         */
        SPECULATIVE,
    }
}
