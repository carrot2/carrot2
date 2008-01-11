package org.carrot2.source.yahoo;

import static org.junit.Assert.*;
import org.junit.Test;

/*
 * 
 */
public class YahooServiceTest
{
    @Test
    public void testNoResultsQuery() throws Exception
    {
        final YahooService service = new YahooService();
        final SearchResponse response = service.query("duiogig oiudgisugviw siug iugw iusviuwg", 0, 100);

        assertEquals(0, response.results.size());
    }

    @Test
    public void testPolishDiacritics() throws Exception
    {
        final YahooService service = new YahooService();
        final SearchResponse response = service.query("Łódź", 0, 100);
        assertEquals(service.serviceParams.maxResultsPerPage, response.results.size());
    }

/*
    public void testLargerQuery() throws Exception
    {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream(
            "yahoo-site-cs.xml"));

        YahooSearchService service = new YahooSearchService(descriptor);
        YahooSearchResult [] result = service.query("apache", 255);
        assertEquals(descriptor.getMaxResultsPerQuery(), result.length);
    }

    public void testFewerThanMaxPerQuery() throws Exception
    {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream(
            "yahoo-site-cs.xml"));

        YahooSearchService service = new YahooSearchService(descriptor);
        YahooSearchResult [] result = service.query("apache", descriptor
            .getMaxResultsPerQuery() / 2);
        assertEquals(descriptor.getMaxResultsPerQuery() / 2, result.length);
    }

    public void testEntities() throws Exception
    {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getClassLoader()
            .getResourceAsStream("resource/yahoo.xml"));

        YahooSearchService service = new YahooSearchService(descriptor);
        YahooSearchResult [] result = service.query("Ala ma kota", 100);

        final Logger logger = Logger.getLogger(YahooSearchServiceTest.class);
        for (int i = 0; i < result.length; i++)
        {
            final String titleSummary = (result[i].title + " " + result[i].summary);
            logger.debug(titleSummary);
            assertTrue(titleSummary.indexOf("&gt;") < 0);
            assertTrue(titleSummary.indexOf("&lt;") < 0);
            assertTrue(titleSummary.indexOf("&amp;") < 0);
        }
    }

    public void testErrorResult() throws Exception
    {
        YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        descriptor.initializeFromXML(this.getClass().getResourceAsStream(
            "yahoo-site-cs.xml"));

        descriptor.setMaxResultsPerQuery(400);

        YahooSearchService service = new YahooSearchService(descriptor);
        try
        {
            service.query("apache", descriptor.getMaxResultsPerQuery() * 2);
            fail();
        }
        catch (IOException e)
        {
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
*/

}
