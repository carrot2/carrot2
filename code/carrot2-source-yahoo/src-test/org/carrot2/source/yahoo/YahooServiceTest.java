package org.carrot2.source.yahoo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.carrot2.core.Document;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests plain service accessor (no queries longer than a single page etc.).
 */
public class YahooServiceTest
{
    private YahooService service;

    @Before
    public void init()
    {
        service = new YahooService();
    }
    
    @Test
    public void testNoResultsQuery() throws Exception
    {
        final SearchResponse response = service.query("duiogig oiudgisugviw siug iugw iusviuwg", 0, 100);
        assertEquals(0, response.results.size());
    }

    @Test
    public void testPolishDiacritics() throws Exception
    {
        final SearchResponse response = service.query("Łódź", 0, 100);
        assertEquals(service.serviceParams.maxResultsPerPage, response.results.size());
    }

    @Test
    public void testLargerQuery() throws Exception
    {
        final int needed = service.serviceParams.maxResultsPerPage / 2;
        final SearchResponse response = service.query("apache", 0, needed);
        assertEquals(needed, response.results.size());
    }

    @Test
    public void testEntities() throws Exception
    {
        final SearchResponse response = service.query("Ala ma kota", 0, 100);

        for (Document d : response.results)
        {
            final String title = d.getField(Document.TITLE);
            final String summary = d.getField(Document.SUMMARY);

            final String merged = (title + " " + summary);

            assertTrue(merged.indexOf("&gt;") < 0);
            assertTrue(merged.indexOf("&lt;") < 0);
            assertTrue(merged.indexOf("&amp;") < 0);
        }
    }

    @Test(expected=IOException.class)
    public void testErrorResult() throws Exception
    {
        service.serviceParams.maxResultsPerPage = 400;
        service.query("apache", 0, 400);
    }
}
