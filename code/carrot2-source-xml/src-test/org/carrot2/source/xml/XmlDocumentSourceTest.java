package org.carrot2.source.xml;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.resource.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

/**
 * Test cases for {@link XmlDocumentSource}.
 */
@RunWith(AnnotationRunner.class)
public class XmlDocumentSourceTest extends DocumentSourceTestBase<XmlDocumentSource>
{
    private ResourceUtils resourceUtils = ResourceUtilsFactory.getDefaultResourceUtils();

    @Override
    public Class<XmlDocumentSource> getComponentClass()
    {
        return XmlDocumentSource.class;
    }

    @Test
    public void testLegacyXml()
    {
        Resource resource = resourceUtils.getFirst("/xml/apple-computer.xml",
            XmlDocumentSourceTest.class);

        attributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "resource"),
            resource);
        final int documentCount = runQuery();
        assertEquals(200, documentCount);
        assertEquals("apple computer", attributes.get(AttributeNames.QUERY));
    }

    @Test
    public void testResultsTruncation()
    {
        Resource resource = resourceUtils.getFirst("/xml/apple-computer.xml",
            XmlDocumentSourceTest.class);

        attributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "resource"),
            resource);
        attributes.put(AttributeNames.RESULTS, 50);
        final int documentCount = runQuery();
        assertEquals(50, documentCount);
        assertEquals("apple computer", attributes.get(AttributeNames.QUERY));
    }

    @Test
    @Prerequisite(requires = "carrot2XmlFeedTestsEnabled")
    public void testRemoteUrl() throws MalformedURLException
    {
        if (getCarrot2XmlFeedUrlBase() == null)
        {
            // Skip test if no base.
            return;
        }

        Resource resource = new ParameterizedUrlResource(new URL(
            getCarrot2XmlFeedUrlBase() + "&q=${query}&results=${results}"));
        final String query = "apple computer";

        attributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "resource"),
            resource);
        attributes.put(AttributeNames.QUERY, query);
        attributes.put(AttributeNames.RESULTS, 50);
        final int documentCount = runQuery();
        assertEquals(50, documentCount);
        assertEquals(query, attributes.get(AttributeNames.QUERY));
    }
}
