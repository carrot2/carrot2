
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

package org.carrot2.input.pubmed;

import java.io.*;

import org.carrot2.core.test.ExternalApiTestBase;

import junit.framework.*;

/**
 * @author Stanislaw Osinski
 */
public class PubMedSearchServiceTest extends ExternalApiTestBase
{
    public void testNoHitsQuery() throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult [] results = service.query("qwieur sldkfjy sdfb", 20);
        assertEquals("Returned results", results.length, 0);
    }

    public void testNonEmptyAbstracts() throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult [] results = service.query("test", 50);
        assertTrue("Returned results", results.length >= 40 && results.length <= 50);

        for (int i = 0; i < results.length; i++)
        {
            if (results[i].summary != null && results[i].summary.length() > 0)
            {
                return;
            }
        }

        fail("No abstracts fetched.");
    }

    public void testMediumQuery() throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult [] results = service.query("test", 100);
        assertTrue("Returned results", results.length >= 80 && results.length <= 100);
    }

    public void testLargeQuery() throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult [] results = service.query("test", 400);
        assertTrue("Returned results", results.length >= 320 && results.length <= 400);
    }

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(PubMedInputComponentTest.class);
        }
        else
        {
            return new TestSuite();
        }
    }
}
