
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

package org.carrot2.source.boss;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.carrot2.core.test.ExternalApiTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Web service accessor.
 */
@RunWith(AnnotationRunner.class)
public class BossWebSearchServiceTest extends ExternalApiTestBase
{
    private BossSearchService service;

    @Before
    public void init()
    {
        service = new BossWebSearchService();
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
    public void testPolishDiacritics() throws Exception
    {
        final SearchEngineResponse response = service.query("Łódź", 0, 100);
        assertEquals(service.metadata.resultsPerPage, response.results.size());
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testLargerQuery() throws Exception
    {
        final int needed = service.metadata.resultsPerPage / 2;
        final SearchEngineResponse response = service.query("apache", 0, needed);
        assertEquals(needed, response.results.size());
    }

    @Test(expected=IOException.class)
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testErrorResult() throws Exception
    {
        service.metadata = new MultipageSearchEngineMetadata(400, 1000);
        service.appid = "xxx";
        service.query("apache", 0, 100);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testCompressedStreamsUsed() throws Exception
    {
        final SearchEngineResponse response = service.query("apache", 0, 50);
        assertEquals("gzip",
            response.metadata.get(SearchEngineResponse.COMPRESSION_KEY));
    }
}
