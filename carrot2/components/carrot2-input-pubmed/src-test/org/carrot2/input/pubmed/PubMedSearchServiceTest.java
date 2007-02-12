/*
 * Copyright (c) 2004 Poznan Supercomputing and Networking Center
 * 10 Noskowskiego Street, Poznan, Wielkopolska 61-704, Poland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
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
