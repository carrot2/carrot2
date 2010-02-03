
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

import java.io.IOException;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests Web service accessor.
 */
public class BossWebSearchServiceTest
{
    private BossSearchService service;

    @Before
    public void init()
    {
        assumeTrue(externalApiTestsEnabled());
        service = new BossWebSearchService();
    }

    @Test
    public void testNoResultsQuery() throws Exception
    {
        final SearchEngineResponse response = service.query(
            QueryableDocumentSourceTestBase.getNoResultsQuery(), 0, 100);
        assertEquals(0, response.results.size());
    }

    @Test
    public void testPolishDiacritics() throws Exception
    {
        final SearchEngineResponse response = service.query("Łódź", 0, 100);
        assertEquals(service.metadata.resultsPerPage, response.results.size());
    }

    @Test
    public void testLargerQuery() throws Exception
    {
        final int needed = service.metadata.resultsPerPage / 2;
        final SearchEngineResponse response = service.query("apache", 0, needed);
        assertEquals(needed, response.results.size());
    }

    @Test(expected = IOException.class)
    public void testErrorResult() throws Exception
    {
        service.metadata = new MultipageSearchEngineMetadata(400, 1000);
        service.appid = "xxx";
        service.query("apache", 0, 100);
    }

    @Test
    public void testCompressedStreamsUsed() throws Exception
    {
        final SearchEngineResponse response = service.query("apache", 0, 50);
        assertEquals("gzip", response.metadata.get(SearchEngineResponse.COMPRESSION_KEY));
    }
}
