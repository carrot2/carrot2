package org.carrot2.source.xml;

import static org.junit.Assert.assertEquals;

import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.resource.*;
import org.junit.Test;

/**
 * Test cases for {@link XmlDocumentSource}.
 */
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
    }
}
