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

/**
 * Serializes an ODP topic as a single ZIP compressed file.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ZipFileTopicSerializer implements TopicSerializer
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.TopicSerializer#serialize(com.stachoodev.carrot.odp.Topic,
     *      java.lang.String)
     */
    public void serialize(Topic topic, String location) throws IOException
    {
        // Create directories
        File file = new File(location);
        file.getParentFile().mkdirs();

        ZipOutputStream zipOutputStream = new ZipOutputStream(
            new FileOutputStream(file));
        zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
        zipOutputStream.putNextEntry(new ZipEntry("topic"));
        ObjectOutputStream stream = new ObjectOutputStream(zipOutputStream);

        stream.writeObject(topic);

        stream.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.TopicSerializer#deserialize(java.lang.String)
     */
    public Topic deserialize(String location) throws IOException,
        ClassNotFoundException
    {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
            location));
        zipInputStream.getNextEntry();
        ObjectInputStream input = new ObjectInputStream(zipInputStream);

        Topic topic = (Topic) input.readObject();

        input.close();

        return topic;
    }
}