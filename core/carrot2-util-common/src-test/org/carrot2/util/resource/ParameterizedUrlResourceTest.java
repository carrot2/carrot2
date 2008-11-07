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

package org.carrot2.util.resource;

import static junit.framework.Assert.assertEquals;

import java.util.Map;

import org.carrot2.util.resource.ParameterizedUrlResource;
import org.junit.Test;

import com.google.common.collect.Maps;

/**
 * Test cases for {@link ParameterizedUrlResource}.
 */
public class ParameterizedUrlResourceTest
{
    @Test
    public void testNoAttributesToSubstitute()
    {
        final String url = "http://test.com/main?query=${query}&results=${results}";
        final Map<String, Object> attributes = Maps.newHashMap();
        final String processedUrl = ParameterizedUrlResource.substituteAttributes(url,
            attributes);

        assertEquals(url, processedUrl);
    }

    @Test
    public void testTwoAttributesToSubstitute()
    {
        final String url = "http://test.com/main?query=${query}&results=${results}";
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put("query", "test");
        attributes.put("results", "");

        final String processedUrl = ParameterizedUrlResource.substituteAttributes(url,
            attributes);
        final String expectedProcessedUrl = "http://test.com/main?query=test&results=";

        assertEquals(expectedProcessedUrl, processedUrl);
    }
}
