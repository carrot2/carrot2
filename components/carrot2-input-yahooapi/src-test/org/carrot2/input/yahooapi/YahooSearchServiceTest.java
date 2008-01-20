
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

package org.carrot2.input.yahooapi;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.carrot2.core.test.ExternalApiTestBase;

/**
 * Tests REST-type call to Yahoo search service.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class YahooSearchServiceTest extends ExternalApiTestBase {

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
        assertEquals(descriptor.getMaxResultsPerQuery(), result.length);
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

        final Logger logger = Logger.getLogger(YahooSearchServiceTest.class);
        for (int i = 0; i < result.length; i++) {
            final String titleSummary = (result[i].title + " " + result[i].summary);
            logger.debug(titleSummary);
            assertTrue(titleSummary.indexOf("&gt;") < 0);
            assertTrue(titleSummary.indexOf("&lt;") < 0);
            assertTrue(titleSummary.indexOf("&amp;") < 0);
        }
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

    public static Test suite()
    {
        if (isApiTestingEnabled())
        {
            return new TestSuite(YahooSearchServiceTest.class);
        }
        else
        {
            return new TestSuite();
        }
    }
}
