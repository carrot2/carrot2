/**
 * 
 */
package org.carrot2.source.yahoo;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.controller.SimpleController;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class YahooDocumentSourceTest
{
    protected SimpleController controller;
    protected Map<String, Object> parameters;
    protected Map<String, Object> attributes;

    @Before
    public void prepareComponent()
    {
        this.controller = new SimpleController();
        this.parameters = new HashMap<String, Object>();
        this.attributes = new HashMap<String, Object>();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNoResultsQuery() throws Exception
    {
        parameters.put("query", "duiogig oiudgisugviw siug iugw iusviuwg");
        parameters.put("results", 100);
        final ProcessingResult result = controller.process(parameters, attributes, YahooDocumentSource.class);

        final Collection<Document> documents = (Collection<Document>) attributes.get("documents");
        assertSame(result.getDocuments(), documents);
        assertEquals(0, documents.size());
    }

    @Test
    public void testQueryLargerThanPage() throws Exception
    {
        final int needed = new YahooServiceParams().resultsPerPage * 2 + 10;

        parameters.put("query", "apache");
        parameters.put("results", needed);
        final ProcessingResult result = controller.process(parameters, attributes, YahooDocumentSource.class);

        final Collection<Document> documents = (Collection<Document>) attributes.get("documents");
        assertSame(result.getDocuments(), documents);
        assertEquals(needed, documents.size());
    }

    @Test
    public void testResultsTotal() throws Exception
    {
        parameters.put("query", "apache");
        final ProcessingResult result = controller.process(parameters, attributes, YahooDocumentSource.class);

        final String attributeName = YahooDocumentSource.class.getName() + ".resultsTotal";
        assertNotNull(attributes.get(attributeName));
        assertNotNull((Long) attributes.get(attributeName) > 0);
    }

}
