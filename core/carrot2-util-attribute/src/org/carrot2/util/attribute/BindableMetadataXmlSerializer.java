
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.io.*;
import java.util.ArrayList;

import com.google.common.collect.Lists;

/**
 * Builds metadata XML files for the requested Java source directories. This is a POJO ANT
 * task as well as a command-line executable Java class.
 */
public class BindableMetadataXmlSerializer
{
    private final ArrayList<File> sourceDirs = Lists.newArrayList();
    private final ArrayList<File> commonNamesSource = Lists.newArrayList();
    private File destinationDir;

    public void setSrc(File sourceDir)
    {
        sourceDirs.add(sourceDir);
    }

    public void setToDir(File destinationDir)
    {
        this.destinationDir = destinationDir;
    }

    public void setCommonNames(File file)
    {
        commonNamesSource.add(file);
    }

    public void execute() throws IOException
    {
        if (destinationDir == null || !destinationDir.isDirectory())
        {
            throw new IllegalArgumentException("Missing destination directory.");
        }

        final BindableMetadataBuilder builder = new BindableMetadataBuilder();
        for (File f : sourceDirs) builder.addSource(f);
        for (File f : commonNamesSource) builder.addCommonMetadataSource(f);
        
        builder.addListener(
            new BindableMetadataBuilderListener.XmlSerializerListener(destinationDir));
        builder.buildAttributeMetadata();
    }

    public static void main(String [] args) throws FileNotFoundException, IOException
    {
        if (args.length < 2)
        {
            printUsage();
            return;
        }

        BindableMetadataXmlSerializer serializer = new BindableMetadataXmlSerializer();
        serializer.setSrc(new File(args[0]));
        serializer.setToDir(new File(args[1]));

        if (args.length > 2)
        {
            for (int i = 2; i < args.length; i++) serializer.setCommonNames(new File(args[i]));
        }
    }

    private static void printUsage()
    {
        System.out.println("Usage: BindableMetadataXmlSerializer "
            + "java-source-dir-or-file output-dir [common-metadata-class-name...]");
    }
}
