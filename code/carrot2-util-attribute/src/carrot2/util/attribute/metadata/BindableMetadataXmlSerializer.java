/**
 *
 */
package carrot2.util.attribute.metadata;

import java.io.File;

/**
 *
 */
public class BindableMetadataXmlSerializer
{
    public static void main(String [] args)
    {
        final BindableMetadataBuilder builder = new BindableMetadataBuilder();

        for (final String path : args)
        {
            builder.addSourceTree(new File(path));
        }

        builder.addListener(new XmlSerializerBindableMetadataBuilderListener());
        builder.buildAttributeMetadata();
    }
}
