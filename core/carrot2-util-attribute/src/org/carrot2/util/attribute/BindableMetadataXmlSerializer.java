package org.carrot2.util.attribute;

import java.io.File;

/**
 * Builds metadata XML files for the requested Java source directories.
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

        builder
            .addListener(new BindableMetadataBuilderListener.XmlSerializerListener());
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
