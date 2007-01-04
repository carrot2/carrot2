
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

package org.carrot2.input.yahooapi;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * Tests REST-type call to Yahoo search service.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class YahooSearchServiceTest extends TestCase {

	public YahooSearchServiceTest(String s) {
		super(s);
	}

    public void testNoResultsQuery() throws Exception {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo-site-cs.xml"));

        YahooSearchService service = new YahooSearchService(descriptor); 
        YahooSearchResult [] result = service.query("duiogig oiudgisugviw siug iugw iusviuwg", 255);
        assertEquals(0, result.length);
    }

	public void testLargerQuery() throws Exception {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo-site-cs.xml"));

        YahooSearchService service = new YahooSearchService(descriptor); 
        YahooSearchResult [] result = service.query("apache", 255);
        assertEquals(255, result.length);
	}
    
	public void testFewerThanMaxPerQuery() throws Exception {
	    YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
	    descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo-site-cs.xml"));
	    
	    YahooSearchService service = new YahooSearchService(descriptor); 
	    YahooSearchResult [] result = service.query("apache", descriptor.getMaxResultsPerQuery() / 2);
	    assertEquals(descriptor.getMaxResultsPerQuery() / 2, result.length);
	}

    public void testEntities() throws Exception {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(
                this.getClass().getClassLoader().getResourceAsStream("resource/yahoo.xml"));

        YahooSearchService service = new YahooSearchService(descriptor); 
        YahooSearchResult [] result = service.query("Ala ma kota", 100);

        for (int i = 0; i < result.length; i++) {
            final String titleSummary = (result[i].title + " " + result[i].summary);
            Logger.getRootLogger().info(titleSummary);
            assertTrue(titleSummary.indexOf("&gt;") < 0);
            assertTrue(titleSummary.indexOf("&lt;") < 0);
            assertTrue(titleSummary.indexOf("&amp;") < 0);
        }
    }
    
	public void testStartFromBug() throws Exception {
	    YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
	    descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo-site-cs.xml"));
	    
	    YahooSearchService service = new YahooSearchService(descriptor); 
	    YahooSearchResult [] result = service.query("apache", descriptor.getMaxResultsPerQuery() * 2);
        
        for (int i = 0; i < descriptor.getMaxResultsPerQuery(); i++)
        {
            final String summary = result[i].summary + "";
            final String summaryOffset = result[i
                + descriptor.getMaxResultsPerQuery()].summary
                + "";

            if (!summary.equals(summaryOffset))
            {
                return;
            }
        }
        
        fail();
	}
    
    public void testErrorResult() throws Exception {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo-site-cs.xml"));

        descriptor.setMaxResultsPerQuery(400);

        YahooSearchService service = new YahooSearchService(descriptor); 
        try {
            service.query("apache", descriptor.getMaxResultsPerQuery() * 2);
            fail();
        } catch (IOException e) {
            // expected, good.
        }
    }
}
