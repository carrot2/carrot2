/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp;

import java.io.*;
import java.util.zip.*;

import com.stachoodev.util.common.*;

/**
 * Serializes multiple ODP topics into a single ZIP compressed file. The
 * physical name of the file is calculated as
 * <code>catid % {@link #modulo}</code>, whereas the files entries are full
 * <code>catid</code>s. Note that due to continuous repacking of the output
 * ZIP files, performance of the {@link #serialize(Topic, String)}method may be
 * poor. With small values of {@link #modulo}the performance of the
 * {@link #deserialize(String)}method may be also affected.
 * 
 * Important: for this class to work the file names given as the last part of
 * the location path <b>must </b> be integer number (e.g. <code>catid</code>
 * s).
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ZipFilesTopicSerializer implements TopicSerializer
{
    /** */
    public final static int modulo = 10;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.TopicSerializer#serialize(com.stachoodev.carrot.odp.Topic,
     *      java.lang.String)
     */
    public void serialize(Topic topic, String location) throws IOException
    {
        File file = new File(location);
        File parent = file.getParentFile();
        String entryName = file.getName();
        String fileName = file.getName();
        try
        {
            int code = Integer.parseInt(fileName);
            fileName = Integer.toString(code % modulo);
            file = new File(parent, fileName);
        }
        catch (NumberFormatException e)
        {
            // Just use the original name and store one topic per file
        }

        // Check if the file exists
        ObjectOutputStream output;
        ZipEntry zipEntry = new ZipEntry(entryName);
        if (file.exists())
        {

            ZipOutputStream zipOutput = ZipUtils.append(file, zipEntry);
            if (zipOutput == null)
            {
                throw new RuntimeException(
                    "Problems with appending to a ZIP file");
            }
            output = new ObjectOutputStream(zipOutput);
        }
        else
        {
            // Create directories
            file.getParentFile().mkdirs();

            ZipOutputStream zipOutputStream = new ZipOutputStream(
                new FileOutputStream(file));
            zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
            zipOutputStream.putNextEntry(zipEntry);
            output = new ObjectOutputStream(zipOutputStream);
        }

        output.writeObject(topic);

        output.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.TopicSerializer#deserialize(java.lang.String)
     */
    public Topic deserialize(String location) throws IOException,
        ClassNotFoundException
    {
        File file = new File(location);
        File parent = file.getParentFile();
        String entryName = file.getName();
        String fileName = file.getName();
        try
        {
            int code = Integer.parseInt(fileName);
            fileName = Integer.toString(code % modulo);
            file = new File(parent, fileName);
        }
        catch (NumberFormatException e)
        {
            // Just use the original name and store one topic per file
        }

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
            file));

        ZipEntry entry;
        do
        {
            entry = zipInputStream.getNextEntry();
            if (entry == null)
            {
                throw new RuntimeException(
                    "Problems with finding ZIP file entry");
            }
        }
        while (!entry.getName().equals(entryName));

        ObjectInputStream input = new ObjectInputStream(zipInputStream);
        Topic topic = (Topic) input.readObject();
        input.close();

        return topic;
    }
}