
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.io.*;

/**
 * Builds metadata XML files for the requested Java source directories.
 */
public class BindableMetadataXmlSerializer
{
    public static void main(String [] args) throws FileNotFoundException, IOException
    {
        if (args.length < 2)
        {
            printUsage();
            return;
        }

        final String javaSource = args[0];
        final String outputDir = args[1];

        final BindableMetadataBuilder builder = new BindableMetadataBuilder();
        builder.addSource(new File(javaSource));

        for (int i = 2; i < args.length; i++)
        {
            builder.addCommonMetadataSource(new File(args[i]));
        }

        builder.addListener(new BindableMetadataBuilderListener.XmlSerializerListener(
            new File(outputDir)));
        builder.buildAttributeMetadata();
    }

    private static void printUsage()
    {
        System.out.println("Usage: BindableMetadataXmlSerializer "
            + "java-source-dir-or-file output-dir [common-metadata-class-name...]");
    }
}
