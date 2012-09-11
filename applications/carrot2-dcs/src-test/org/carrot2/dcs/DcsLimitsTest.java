/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import org.carrot2.util.tests.UsesExternalServices;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * Test cases for the {@link DcsApp}.
 */
public class DcsLimitsTest extends DcsTestBase
{
    String dcsConfigPath()
    {
        return "src-test/xml/dcs.2rps.xml";
    }

    @Test(expected = UniformInterfaceException.class)
    @UsesExternalServices
    public void getNoQuerySpecified() throws Exception
    {
        try
        {
            requestExternalSource(Method.GET, xmlUrl, "query", "test");
            requestExternalSource(Method.GET, xmlUrl, "query", "test");
            requestExternalSource(Method.GET, xmlUrl, "query", "test");
            requestExternalSource(Method.GET, xmlUrl, "query", "test");
        }
        catch (UniformInterfaceException e)
        {
            assertThat(e.getResponse().getStatus()).isEqualTo(403);
            throw e;
        }
    }
}
