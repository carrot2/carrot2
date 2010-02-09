
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.boss;

import static org.carrot2.core.test.ExternalApiTestAssumptions.externalApiTestsEnabled;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.source.SearchEngineResponse;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests News service accessor.
 */
public class BossNewsSearchServiceTest
{
    private BossSearchService service;

    @Before
    public void init()
    {
        assumeTrue(externalApiTestsEnabled());
        service = new BossNewsSearchService();
    }

    @Test
    public void testNoResultsQuery() throws Exception
    {
        final SearchEngineResponse response = service.query(
            QueryableDocumentSourceTestBase.getNoResultsQuery(), 0, 100);
        assertEquals(0, response.results.size());
    }

    @Test
    public void testPresidentQuery() throws Exception
    {
        final int needed = service.metadata.resultsPerPage / 2;
        final SearchEngineResponse response = service.query("president", 0, needed);
        assertEquals(needed, response.results.size());
    }
}
