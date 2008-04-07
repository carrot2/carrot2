package org.carrot2.source.xml;

import static junit.framework.Assert.assertEquals;

import java.util.Map;

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
        final String processedUrl = ParameterizedUrlResource.substituteAttributes(
            attributes, url);

        assertEquals(url, processedUrl);
    }

    @Test
    public void testTwoAttributesToSubstitute()
    {
        final String url = "http://test.com/main?query=${query}&results=${results}";
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put("query", "test");
        attributes.put("results", "");

        final String processedUrl = ParameterizedUrlResource.substituteAttributes(
            attributes, url);
        final String expectedProcessedUrl = "http://test.com/main?query=test&results=";

        assertEquals(expectedProcessedUrl, processedUrl);
    }
}
