package org.carrot2.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 
 */
public class ControllerTest
{
    @Test
    public void testProcess() throws InstantiationException
    {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("numDocs", Integer.valueOf(5));
        params.put("query", "query");

        final TestDocumentSource source = new TestDocumentSource();
        final TestClusteringAlgorithm algorithm = new TestClusteringAlgorithm();

        final Controller controller = new ControllerImpl();
        final ProcessingResult result = controller.process(params, null, source, algorithm);

        assertEquals("query", source.query);        
        assertEquals(5, source.numDocs);
        assertEquals(5, algorithm.documents.size());

        assertNotNull(result);
        assertNotNull(result.getDocuments());
        assertEquals(5, result.getDocuments().size());
    }
}
