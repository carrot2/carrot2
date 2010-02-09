
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

package org.carrot2.source.yahoo;

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
 * Tests plain service accessor (no queries longer than a single page etc.).
 */
public class YahooWebSearchServiceTest
{
    private YahooSearchService service;

    @Before
    public void init()
    {
        service = new YahooWebSearchService();
    }

    @Test
    public void testNoResultsQuery() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        final SearchEngineResponse response = service.query(
            QueryableDocumentSourceTestBase.getNoResultsQuery(), 0, 100);
        assertEquals(0, response.results.size());
    }

    @Test
    public void testPolishDiacritics() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        final SearchEngineResponse response = service.query("Łódź", 0, 100);
        assertEquals(service.metadata.resultsPerPage, response.results.size());
    }

    @Test
    public void testLargerQuery() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        final int needed = service.metadata.resultsPerPage / 2;
        final SearchEngineResponse response = service.query("apache", 0, needed);
        assertEquals(needed, response.results.size());
    }

    @Test(expected = IOException.class)
    public void testErrorResult() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        service.metadata = new MultipageSearchEngineMetadata(400, 1000);
        service.query("apache", 0, 400);
    }

    @Test
    public void testCompressedStreamsUsed() throws Exception
    {
        assumeTrue(externalApiTestsEnabled());
        final SearchEngineResponse response = service.query("apache", 0, 50);
        assertEquals("gzip", response.metadata.get(SearchEngineResponse.COMPRESSION_KEY));
    }
}
