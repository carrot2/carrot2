package org.carrot2.source.boss;

import static org.junit.Assert.assertEquals;

import org.carrot2.core.test.ExternalApiTestBase;
import org.carrot2.source.SearchEngineResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests News service accessor.
 */
@RunWith(AnnotationRunner.class)
public class BossNewsSearchServiceTest extends ExternalApiTestBase
{
    private BossSearchService service;

    @Before
    public void init()
    {
        service = new BossNewsSearchService();
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testNoResultsQuery() throws Exception
    {
        final SearchEngineResponse response = service.query(ExternalApiTestBase.NO_RESULTS_QUERY, 0, 100);
        assertEquals(0, response.results.size());
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testPresidentQuery() throws Exception
    {
        final int needed = service.metadata.resultsPerPage / 2;
        final SearchEngineResponse response = service.query("president", 0, needed);
        assertEquals(needed, response.results.size());
    }
}
