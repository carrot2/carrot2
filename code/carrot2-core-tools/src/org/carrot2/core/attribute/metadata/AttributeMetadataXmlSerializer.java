/**
 * 
 */
package org.carrot2.core.attribute.metadata;

import java.io.File;

/**
 *
 */
public class AttributeMetadataXmlSerializer
{
    public static void main(String [] args)
    {
        AttributeMetadataBuilder builder = new AttributeMetadataBuilder();

        for (String path : args)
        {
            builder.addSourceTree(new File(path));
        }

        builder.addListener(new XmlSerializerAttributeMetadataBuilderListener());
        builder.buildAttributeMetadata();
    }
}
