
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.pubmed;

import java.io.*;

import junit.framework.*;


/**
 * @author Stanislaw Osinski
 */
public class PubMedSearchServiceTest
    extends TestCase
{
    public void testNoHitsQuery()
        throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult[] results = service.query("qwieur sldkfjy sdfb", 20);
        assertEquals("Returned results", results.length, 0);
    }


    public void testNonEmptyAbstracts()
        throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult[] results = service.query("test", 50);
        assertEquals("Returned results", results.length, 50);
        
        for (int i = 0; i < results.length; i++) {
            if (results[i].summary != null && results[i].summary.length() > 0)
            {
                return;
            }
        }
        
        fail("No abstracts fetched.");
    }
    
    public void testSmallQuery()
    throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult[] results = service.query("test", 50);
        assertEquals("Returned results", results.length, 50);
    }


    public void testMediumQuery()
        throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult[] results = service.query("test", 100);
        assertEquals("Returned results", results.length, 100);
    }


    public void testLargeQuery()
        throws IOException
    {
        PubMedSearchService service = new PubMedSearchService();
        PubMedSearchResult[] results = service.query("test", 400);
        assertEquals("Returned results", results.length, 400);
    }
}
