
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.fetcher;

import java.util.ArrayList;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.fetcher.DebugParallelFetcher.FetchInfo;

/**
 * Tests {@link ParallelFetcher} class.
 * 
 * @author Dawid Weiss
 */
public class TestParallelFetcher extends TestCase
{
    /**
     * 
     */
    public void testTypicalSituation() throws ProcessingException
    {
        DebugParallelFetcher[] fetchers = runBoth(
            0, 97,            // start index, requested results
            200, 50,          // max result index, single fetch size,
            1000              // total number of results available
        );

        // Check fetch infos for prefetch version.
        FetchInfo [] fetchInfos = fetchers[0].getFetchInfos();
        assertEquals(2, fetchInfos.length);
        assertEquals(0, fetchInfos[0].startAt);
        assertEquals(50, fetchInfos[0].fetchSize);
        assertEquals(50, fetchInfos[1].startAt);
        assertEquals(47, fetchInfos[1].fetchSize);

        // Check fetch infos for parallel version.
        fetchInfos = fetchers[1].getFetchInfos();
        assertEquals(2, fetchInfos.length);
        assertEquals(0, fetchInfos[0].startAt);
        assertEquals(50, fetchInfos[0].fetchSize);
        assertEquals(50, fetchInfos[1].startAt);
        assertEquals(47, fetchInfos[1].fetchSize);
    }

    /**
     * Limited number of results is available from a search engine.
     */
    public void testLimitedResults() throws ProcessingException
    {
        runBoth(
            0, 100,           // start index, requested results
            50, 10,           // max result index, single fetch size,
            1000              // total number of results available
        );
    }

    /**
     * Limited number of results is available for a query.
     */
    public void testSmallResultSet() throws ProcessingException
    {
        DebugParallelFetcher[] fetchers = runBoth(
            0, 175,           // start index, requested results
            400, 50,          // max result index, single fetch size,
            16                // total number of results available
        );

        // This is not a mistake -- the first request
        // is always for the full page in the prefetch version.
        FetchInfo [] fetchInfos = fetchers[0].getFetchInfos();
        assertEquals(1, fetchInfos.length);
        assertEquals(50, fetchInfos[0].fetchSize);

        // In parallel mode the total is not visible, so multiple
        // requests are made.
        fetchInfos = fetchers[1].getFetchInfos();
        assertEquals(4, fetchInfos.length);
        assertEquals(0, fetchInfos[0].startAt);
        assertEquals(50, fetchInfos[0].fetchSize);

        assertEquals(50, fetchInfos[1].startAt);
        assertEquals(50, fetchInfos[1].fetchSize);

        assertEquals(100, fetchInfos[2].startAt);
        assertEquals(50, fetchInfos[2].fetchSize);
        
        assertEquals(150, fetchInfos[3].startAt);
        assertEquals(25, fetchInfos[3].fetchSize);
    }

    /**
     * 
     */
    public void testSmallRequestBigFetchSize() throws ProcessingException
    {
        FetchInfo [] fetchInfos = runWithPrefetch(
            0, 50,            // start index, requested results
            400, 200,         // max result index, single fetch size,
            1000              // total number of results available
        ).getFetchInfos();

        // We should assert here that only one 50-result request was made.
        assertEquals(1, fetchInfos.length);
        assertEquals(0, fetchInfos[0].startAt);
        assertEquals(50, fetchInfos[0].fetchSize);
    }

    /**
     * Start is negative.
     */
    public void testNegativeStart() throws ProcessingException
    {
        try {
            runBoth(
                -10, 100,         // start index, requested results
                400, 50,          // max result index, single fetch size,
                1000              // total number of results available
            );
            fail();
        } catch (ProcessingException e) {
            // expected.
        }
    }

    /**
     * 
     */
    private DebugParallelFetcher runWithPrefetch(int startAtIndex, int requestedResults,
        int maxResultIndex, int singleFetchSize, int totalEstimated) 
        throws ProcessingException
    {
        return run(startAtIndex, requestedResults, maxResultIndex, singleFetchSize, 
            totalEstimated, false);
    }

    /**
     * @return Returns two {@link DebugParallelFetcher}s: (prefetch, parallel).
     */
    private DebugParallelFetcher [] runBoth(int startAtIndex, int requestedResults,
        int maxResultIndex, int singleFetchSize, int totalEstimated) 
        throws ProcessingException
    {
        return new DebugParallelFetcher [] {
            run(startAtIndex, requestedResults, maxResultIndex, singleFetchSize, 
                totalEstimated, false),
            run(startAtIndex, requestedResults, maxResultIndex, singleFetchSize, 
                totalEstimated, true)
        };
    }

    /**
     * 
     */
    private DebugParallelFetcher run(int startAtIndex, int requestedResults,
        int maxResultIndex, int singleFetchSize, int totalEstimated,
        boolean prefetch) 
        throws ProcessingException
    {
        final ArrayList expectedResults = new ArrayList();

        final DebugParallelFetcher pfetcher = createParallelFetcher(
            expectedResults,
            startAtIndex, requestedResults, 
            maxResultIndex, singleFetchSize, totalEstimated);

        // Perform initial check for totalEstimated.
        pfetcher.setParallelMode(prefetch);

        // Run fetchers and push results.
        pfetcher.fetch();

        // Verify the output.
        ArrayAssert.assertEquivalenceArrays(expectedResults.toArray(), 
            pfetcher.getResults().toArray());
        
        return pfetcher;
    }

    /**
     * Creates test data and fetcher.
     */
    private DebugParallelFetcher createParallelFetcher(ArrayList expectedResults,
        int startAt, int requestedResults, int maxResults, int perQueryResults, long totalEstimated)
    {
        // Create 'expected' results.
        final int maxId = Math.min((int) Math.min(maxResults, totalEstimated), startAt + requestedResults);
        final int minId = Math.min((int) Math.min(maxResults, totalEstimated), Math.max(0, startAt));
        for (int i = minId; i < maxId; i++)
        {
            expectedResults.add(DebugParallelFetcher.createRawDocument(i));
        }

        // Create parallel fetcher.
        final DebugParallelFetcher pfetcher = new DebugParallelFetcher(
            "Test", "query", startAt, requestedResults, maxResults, perQueryResults,
            totalEstimated);

        return pfetcher;
    }
}
