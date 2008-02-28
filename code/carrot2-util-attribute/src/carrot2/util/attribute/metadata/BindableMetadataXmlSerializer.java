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
            // TODO: remove this nasty hack at some point
            if (isClassName(path))
            {
                builder.addCommonMetadataSource(path);
            }
            else
            {
                builder.addSourceTree(new File(path));
            }
        }

        builder.addListener(new XmlSerializerBindableMetadataBuilderListener());
        builder.buildAttributeMetadata();
    }

    private static boolean isClassName(String string)
    {
        try
        {
            Class.forName(string);
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }

        return true;
    }
}
