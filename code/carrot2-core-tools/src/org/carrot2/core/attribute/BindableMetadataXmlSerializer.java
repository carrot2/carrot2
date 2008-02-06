/**
 * 
 */
package org.carrot2.core.attribute;

import java.io.File;

/**
 *
 */
public class BindableMetadataXmlSerializer
{
    public static void main(String [] args)
    {
        BindableMetadataBuilder builder = new BindableMetadataBuilder();

        for (String path : args)
        {
            builder.addSourceTree(new File(path));
        }

        builder.addListener(new XmlSerializerBindableMetadataBuilderListener());
        builder.buildAttributeMetadata();
    }
}
