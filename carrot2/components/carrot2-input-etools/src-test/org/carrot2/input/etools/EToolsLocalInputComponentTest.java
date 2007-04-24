
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

package org.carrot2.input.etools;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.test.*;

/**
 * eTools input component tests.
 * 
 * @author Stanislaw Osinski
 */
public class EToolsLocalInputComponentTest extends LocalInputComponentTestBase
{
    public EToolsLocalInputComponentTest(String name)
    {
        super(name);
    }

    final LocalComponentFactory inputFactory = new LocalComponentFactory()
    {
        public LocalComponent getInstance()
        {
            return new EToolsLocalInputComponent("Carrot2");
        }
    };

    protected LocalComponentFactory getLocalInputFactory()
    {
        return inputFactory;
    }

    public void testDataSourceResultsCount()
    {
        EToolsLocalInputComponent input = new EToolsLocalInputComponent("partnerId");
        Map params = new HashMap();

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "0");
        assertEquals("Data source results count", 0, input.getDataSourceResultsCount(params));

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "50");
        assertEquals("Data source results count", 20, input.getDataSourceResultsCount(params));

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "100");
        assertEquals("Data source results count", 20, input.getDataSourceResultsCount(params));

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "120");
        assertEquals("Data source results count", 30, input.getDataSourceResultsCount(params));

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "200");
        assertEquals("Data source results count", 30, input.getDataSourceResultsCount(params));

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "250");
        assertEquals("Data source results count", 40, input.getDataSourceResultsCount(params));

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "400");
        assertEquals("Data source results count", 40, input.getDataSourceResultsCount(params));

        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, "450");
        assertEquals("Data source results count", 40, input.getDataSourceResultsCount(params));
    }

    public void testNoHitsQuery() throws Exception
    {
        performQuery("asdhasd alksjdhar swioer", 50, 0);
    }

    public void testSmallQuery() throws Exception
    {
        performQuery("test", 50, 50);
    }

    public void testMediumQuery() throws Exception
    {
        performQuery("test", 100, new Range(75, 100));
    }

    public void testLargeQuery() throws Exception
    {
        performQuery("test", 400, new Range(150, 400));
    }

    public void testUniqueIds() throws Exception
    {
        performIdUniquenessTest("test", 200);
    }

    /**
     * Make sure that the results contain information about the source.
     * 
     * @throws Exception
     */
    public void testSources() throws Exception
    {
        List results = query("test", 50);

        for (Iterator it = results.iterator(); it.hasNext();)
        {
            RawDocument document = (RawDocument) it.next();
            String [] sources = (String []) document.getProperty(RawDocument.PROPERTY_SOURCES);

            assertNotNull("Sources information available", sources);
            assertTrue("Non-zero number of sources", sources.length > 0);
        }
    }
}
