package org.carrot2.core.fetcher;

import java.util.ArrayList;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.clustering.RawDocumentBase;

/**
 * Tests {@link ParallelFetcher} class.
 * 
 * @author Dawid Weiss
 */
public class TestParallelFetcher extends TestCase
{
    /**
     * Simple scenario: just fetch using multiple threads.
     */
    public void testSimpleFetch() throws ProcessingException
    {
        // Prepare fetcher.
        final int maxResults = 100;
        final int requestedResults = 97;
        final int startAt = 0;
        final int perQueryResults = 10;
        final int totalEstimated = 1000;

        final ArrayList expectedResults = new ArrayList();
        final ArrayList fetchedResults = new ArrayList();
        final ParallelFetcher pfetcher = createParallelFetcher(expectedResults, fetchedResults, startAt,
            requestedResults, maxResults, perQueryResults, totalEstimated);

        // Run fetchers and push results.
        pfetcher.fetch();

        ArrayAssert.assertEquivalenceArrays(expectedResults.toArray(), fetchedResults.toArray());
    }

    /**
     * Limited number of results is available from a search engine.
     */
    public void testLimitedResults() throws ProcessingException
    {
        // Prepare fetcher.
        final int maxResults = 50;
        final int requestedResults = 100;
        final int startAt = 0;
        final int perQueryResults = 40;
        final int totalEstimated = 1000;

        final ArrayList expectedResults = new ArrayList();
        final ArrayList fetchedResults = new ArrayList();
        final ParallelFetcher pfetcher = createParallelFetcher(expectedResults, fetchedResults, startAt,
            requestedResults, maxResults, perQueryResults, totalEstimated);

        // Run fetchers and push results.
        pfetcher.fetch();

        ArrayAssert.assertEquivalenceArrays(expectedResults.toArray(), fetchedResults.toArray());
    }

    /**
     * Limited number of results is available for a query.
     */
    public void testSmallResultSet() throws ProcessingException
    {
        // Prepare fetcher.
        final int maxResults = 400;
        final int requestedResults = 200;
        final int startAt = 0;
        final int perQueryResults = 20;
        final int totalEstimated = 16;

        final ArrayList expectedResults = new ArrayList();
        final ArrayList fetchedResults = new ArrayList();
        final ParallelFetcher pfetcher = createParallelFetcher(expectedResults, fetchedResults, startAt,
            requestedResults, maxResults, perQueryResults, totalEstimated);

        // Run fetchers and push results.
        pfetcher.fetch();

        ArrayAssert.assertEquivalenceArrays(expectedResults.toArray(), fetchedResults.toArray());
    }

    /**
     * Start is negative.
     */
    public void testNegativeStart() throws ProcessingException
    {
        // Prepare fetcher.
        final int maxResults = 400;
        final int requestedResults = 100;
        final int startAt = -10;
        final int perQueryResults = 20;
        final int totalEstimated = 1000;

        final ArrayList expectedResults = new ArrayList();
        final ArrayList fetchedResults = new ArrayList();
        final ParallelFetcher pfetcher = createParallelFetcher(expectedResults, fetchedResults, startAt,
            requestedResults, maxResults, perQueryResults, totalEstimated);

        // Run fetchers and push results.
        try {
            pfetcher.fetch();
            fail();
        } catch (ProcessingException e) {
            // expected.
        }
    }

    /**
     * Creates test data and fetcher.
     */
    private ParallelFetcher createParallelFetcher(ArrayList expectedResults, final ArrayList fetchedResults,
        int startAt, int requestedResults, int maxResults, final int perQueryResults, final int totalEstimated)
    {
        // create 'expected' results.
        final int maxId = Math.min(Math.min(maxResults, totalEstimated), startAt + requestedResults);
        final int minId = Math.min(Math.min(maxResults, totalEstimated), Math.max(0, startAt));
        for (int i = minId; i < maxId; i++)
        {
            expectedResults.add(createRD(i));
        }

        // create parallel fetcher.
        final ParallelFetcher pfetcher = new ParallelFetcher("Test", "query", startAt, requestedResults, maxResults)
        {
            public SingleFetcher getFetcher()
            {
                return new SingleFetcher()
                {
                    public SearchResult fetch(String query, int startAt) throws ProcessingException
                    {
                        final RawDocument [] rawDocs = new RawDocument [perQueryResults];
                        int j = 0;
                        for (int i = startAt; i < startAt + perQueryResults; i++, j++)
                        {
                            rawDocs[j] = createRD(i);
                        }

                        return new SearchResult(rawDocs, startAt, totalEstimated);
                    }
                };
            }

            /**
             *
             */
            public void pushResults(int at, final RawDocument rawDocument) throws ProcessingException
            {
                fetchedResults.add(rawDocument);
            }
        };

        return pfetcher;
    }

    /**
     */
    private RawDocument createRD(int i)
    {
        final Integer id = new Integer(i);

        return new RawDocumentBase("nourl://" + i, "title", "snippet")
        {
            public Object getId()
            {
                return id;
            }
            
            public boolean equals(Object obj)
            {
                if (obj instanceof RawDocumentBase) {
                    
                }
                return this.getId().equals(((RawDocumentBase) obj).getId());
            }
            
            public int hashCode()
            {
                return this.getId().hashCode();
            }
        };
    }
}
