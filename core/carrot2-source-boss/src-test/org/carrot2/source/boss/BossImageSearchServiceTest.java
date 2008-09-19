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
 * Tests image service accessor.
 */
@RunWith(AnnotationRunner.class)
public class BossImageSearchServiceTest extends ExternalApiTestBase
{
    private BossSearchService service;

    @Before
    public void init()
    {
        service = new BossImageSearchService();
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testImageQuery() throws Exception
    {
        final int needed = service.metadata.resultsPerPage / 2;
        final SearchEngineResponse response = service.query("apple", 0, needed);
        assertEquals(needed, response.results.size());
    }
}
