
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

package org.carrot2.input.etools;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.carrot2.core.LocalInputComponent;

/**
 * eTools input component tests.
 *
 * @author Stanislaw Osinski
 */
public class EToolsLocalInputComponentStaticTest extends TestCase
{
    public EToolsLocalInputComponentStaticTest(String name)
    {
        super(name);
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
}
