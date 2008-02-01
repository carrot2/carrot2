/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.carrot2.core.attribute.metadata.tests.SimpleComponent;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class AttributeMetadataBuilderTest
{
    private static final String SOURCE_PATH_PROPERTY = "source.path";
    private String sourcePath;

    @Before
    public void prepareSourcePath()
    {
        sourcePath = System.getProperty(SOURCE_PATH_PROPERTY);
        if (sourcePath == null)
        {
            fail("Please provide path to sources of test classes in the '"
                + SOURCE_PATH_PROPERTY + "' JVM property");
        }
    }

    @Test
    public void testSimpleComponent()
    {
        AttributeMetadataBuilder builder = new AttributeMetadataBuilder();
        builder.addSourceTree(new File(sourcePath));

        Map<String, Collection<AttributeMetadata>> metadata = builder
            .buildAttributeMetadata();

        final Collection<AttributeMetadata> attributeMetadata = metadata
            .get(SimpleComponent.class.getName());
        assertNotNull(attributeMetadata);
        assertThat(attributeMetadata).contains(new AttributeMetadata(null, "word", null),
            new AttributeMetadata("Attribute comment", "word2", null),
            new AttributeMetadata("Only title", "multi word label", null));
    }
}
