package com.dawidweiss.carrot.input.yahoo;

import junit.framework.*;

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
}
