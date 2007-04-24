package org.carrot2.core.impl;

import java.io.*;
import java.util.HashMap;

import org.carrot2.core.LocalControllerBase;
import org.carrot2.core.test.ClusteringProcessTestBase;
import org.carrot2.core.test.Range;

/**
 * Tests {@link SaveXmlFilterComponent}.
 * 
 * @author Dawid Weiss
 */
public class SaveXmlFilterComponentTest extends ClusteringProcessTestBase
{
    public SaveXmlFilterComponentTest(String testName)
    {
        super(testName);
    }

    /**
     * 
     */
    protected String [] getFiltersChain(LocalControllerBase controller)
    {
        return new String []
        {
            "filter-save-xml"
        };
    }

    /**
     * Just passthrough.
     */
    public void testPassthrough() throws Exception
    {
        assertResultsInRange("n/a", 100, Range.exact(100), Range.exact(28), null);
    }

    /**
     * Save to a {@link OutputStream}, save both documents and clusters.
     */
    public void testSaveToStream() throws Exception
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HashMap params = new HashMap();
        params.put(SaveFilterComponentBase.PARAM_OUTPUT_STREAM, outputStream);
        params.put(SaveFilterComponentBase.PARAM_SAVE_CLUSTERS, Boolean.TRUE);

        assertResultsInRange("n/a", 100, Range.exact(100), Range.exact(28), params);

        // Attemp to re-parse the result and check conditions.
        final XmlStreamInputComponent.QueryResult result = XmlStreamInputComponent.loadQueryResult(
            new ByteArrayInputStream(outputStream.toByteArray()), 100);
        assertEquals(100, result.rawDocuments.size());
        assertEquals(28, result.rawClusters.size());
    }

    /**
     * Save to a {@link OutputStream}, check default settings.
     */
    public void testCheckDefaults() throws Exception
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HashMap params = new HashMap();
        params.put(SaveFilterComponentBase.PARAM_OUTPUT_STREAM, outputStream);

        assertResultsInRange("n/a", 100, Range.exact(100), Range.exact(28), params);

        // Attemp to re-parse the result and check conditions.
        final XmlStreamInputComponent.QueryResult result = XmlStreamInputComponent.loadQueryResult(
            new ByteArrayInputStream(outputStream.toByteArray()), 100);
        assertEquals(100, result.rawDocuments.size());
        assertEquals(0, result.rawClusters.size());
    }
}

