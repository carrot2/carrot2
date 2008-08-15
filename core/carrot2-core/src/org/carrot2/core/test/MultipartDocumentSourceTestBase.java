package org.carrot2.core.test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.carrot2.core.DocumentSource;
import org.carrot2.source.MultipartSearchEngine;
import org.carrot2.source.SearchEngineStats;
import org.carrot2.source.MultipartSearchEngine.MultipartSearchEngineMetadata;
import org.carrot2.source.MultipartSearchEngine.SearchMode;
import org.junit.Test;
import org.junitext.Prerequisite;

/**
 * Common tests for {@link DocumentSource}s that can make more than one search requests
 * to get results for one query.
 */
public abstract class MultipartDocumentSourceTestBase<T extends DocumentSource> extends
    QueryableDocumentSourceTestBase<T>
{
    /**
     * Metadata for the {@link MultipartSearchEngine} being tested.
     */
    protected abstract MultipartSearchEngineMetadata getSearchEngineMetadata();

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testQueryLargerThanPage() throws Exception
    {
        final int needed = getSearchEngineMetadata().resultsPerPage * 2 + 25;

        // Allow some slack (duplicated URLs).
        final int documentsReturned = runQuery("test", needed);

        assertThat(documentsReturned).isGreaterThan(needed - 10);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testConservativeMode() throws Exception
    {
        processingAttributes.put("search-mode", SearchMode.CONSERVATIVE);

        runAndCheckNoResultsQuery();
        assertEquals(1, processingAttributes.get(SearchEngineStats.class.getName()
            + ".pageRequests"));
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testSpeculativeMode() throws Exception
    {
        processingAttributes.put("search-mode", SearchMode.SPECULATIVE);

        runAndCheckNoResultsQuery(getSearchEngineMetadata().resultsPerPage + 1);
        assertEquals(2, processingAttributes.get(SearchEngineStats.class.getName()
            + ".pageRequests"));
    }
}
