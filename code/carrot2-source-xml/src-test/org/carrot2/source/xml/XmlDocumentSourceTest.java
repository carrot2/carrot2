package org.carrot2.source.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link XmlDocumentSource}.
 */
public class XmlDocumentSourceTest extends DocumentSourceTestBase<XmlDocumentSource>
{
    private static final String FILE_PATH_PREFIX_PROPERTY = "org.carrot2.source.xml.test.file-path-prefix";
    private String filePathPrefix = "src-test";

    @Override
    public Class<XmlDocumentSource> getComponentClass()
    {
        return XmlDocumentSource.class;
    }

    @Before
    public void initFilePrefix() throws IOException
    {
        if (System.getProperty(FILE_PATH_PREFIX_PROPERTY) != null)
        {
            filePathPrefix = System.getProperty(FILE_PATH_PREFIX_PROPERTY);
        }
    }

    @Test
    public void testLegacyXml()
    {
        attributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "path"), new File(
            filePathPrefix + "/xml/apple-computer.xml").getAbsolutePath());
        final int documentCount = runQuery();
        assertEquals(200, documentCount);
    }
}
