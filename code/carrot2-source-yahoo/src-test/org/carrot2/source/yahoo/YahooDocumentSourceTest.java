package org.carrot2.source.yahoo;

import static org.junit.Assert.*;

import java.util.Collection;

import org.carrot2.core.Document;
import org.carrot2.core.DocumentSource;
import org.carrot2.core.DocumentSourceTest;
import org.carrot2.core.parameter.AttributeNames;
import org.carrot2.source.SearchMode;
import org.junit.Test;


/**
 * Tests Yahoo! input component.
 */
public class YahooDocumentSourceTest extends DocumentSourceTest<YahooDocumentSource>
{
    @Test
    public void testNoResultsQuery() throws Exception
    {
        assertEquals(0, runQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100));
    }

    @Test
    public void testQueryLargerThanPage() throws Exception
    {
        final int needed = new YahooWebSearchService().resultsPerPage * 2 + 10;
        // Allow some slack (duplicated URLs).
        assertTrue(runQuery("apache", needed) > needed - 5);
    }

    @Test
    public void testResultsTotal() throws Exception
    {
        runQuery("apache", 50);

        assertNotNull(attributes.get(AttributeNames.RESULTS_TOTAL));
        assertNotNull((Long) attributes.get(AttributeNames.RESULTS_TOTAL) > 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testURLsUnique() throws Exception 
    {
        runQuery("apache", 200);

        assertFieldUnique((Collection<Document>) attributes.get(AttributeNames.DOCUMENTS),
            Document.CONTENT_URL);
    }

    @Test
    public void testConservativeMode() throws Exception
    {
        attributes.put("search-mode", SearchMode.CONSERVATIVE);

        assertEquals(0, runQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100));
        assertEquals(1, (Integer) attributes.get(YahooSearchService.class.getName() + ".requestCount"));
    }

    @Test
    public void testSpeculativeMode() throws Exception
    {
        attributes.put("search-mode", SearchMode.SPECULATIVE);

        assertEquals(0, runQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100));
        assertEquals(2, (Integer) attributes.get(YahooSearchService.class.getName() + ".requestCount"));
    }
    
    @Test
    public void testNewsServiceSearch() throws Exception
    {
        attributes.put(YahooDocumentSource.class.getName() 
            + ".service", YahooNewsSearchService.class);

        assertTrue(runQuery("iraq", 50) > 0);
    }

    @Override
    public Class<? extends DocumentSource> getComponentClass()
    {
        return YahooDocumentSource.class;
    }
}
