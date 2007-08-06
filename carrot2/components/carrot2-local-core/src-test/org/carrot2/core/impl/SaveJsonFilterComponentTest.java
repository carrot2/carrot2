
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

package org.carrot2.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.carrot2.core.LocalControllerBase;
import org.carrot2.core.test.ClusteringProcessTestBase;
import org.carrot2.core.test.Range;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tests {@link SaveJsonFilterComponent}.
 * 
 * @author Dawid Weiss
 */
public class SaveJsonFilterComponentTest extends ClusteringProcessTestBase
{
    public SaveJsonFilterComponentTest(String testName)
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
            "filter-save-json"
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
     * Save to an {@link OutputStream}.
     */
    public void testSaveToStream() throws Exception
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HashMap params = new HashMap();
        params.put(SaveFilterComponentBase.PARAM_OUTPUT_STREAM, outputStream);
        params.put(SaveFilterComponentBase.PARAM_SAVE_CLUSTERS, Boolean.TRUE);

        assertResultsInRange("n/a", 100, Range.exact(100), Range.exact(28), params);

        // Attemp to re-parse the result and check conditions.
        final String jsonOutput = new String(outputStream.toByteArray(), "UTF-8");
        log.debug(jsonOutput);
        final JSONObject jsonObject = new JSONObject(jsonOutput);
        final JSONArray docs = jsonObject.getJSONArray("documents");
        final JSONArray clusters = jsonObject.getJSONArray("clusters");

        assertTrue("Documents expected.", docs != null);
        assertTrue("Clusters expected.", clusters != null);

        assertEquals(100, docs.length());
        assertEquals(28, clusters.length());
    }
}

