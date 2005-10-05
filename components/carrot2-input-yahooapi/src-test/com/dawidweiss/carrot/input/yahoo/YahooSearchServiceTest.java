package com.dawidweiss.carrot.input.yahoo;

import com.dawidweiss.carrot.input.yahoo.YahooSearchResult;
import com.dawidweiss.carrot.input.yahoo.YahooSearchService;
import com.dawidweiss.carrot.input.yahoo.YahooSearchServiceDescriptor;

import junit.framework.TestCase;

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
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo.xml"));

        YahooSearchService service = new YahooSearchService(descriptor); 
        YahooSearchResult [] result = service.query("duiogig oiudgisugviw siug iugw iusviuwg", 255);
        assertEquals(0, result.length);
    }

	public void testLargerQuery() throws Exception {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream("yahoo.xml"));

        YahooSearchService service = new YahooSearchService(descriptor); 
        YahooSearchResult [] result = service.query("apache", 255);
        assertEquals(255, result.length);
	}
}
