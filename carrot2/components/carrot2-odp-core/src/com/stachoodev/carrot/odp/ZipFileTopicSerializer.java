/*
 * ZipFileTopicSerializer.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp;

import java.io.*;
import java.util.zip.*;

/**
 * Serializes an ODP topic as a single ZIP compressed file.
 * 
 * TODO: Could store more categories in one file with more entries in it
 * (fileName = catid % someConstant, entries = catids). However, you can't add
 * entries to an existing zip file, so we would need to rewrite the file every
 * time a new category is added.
 * 
 * @author stachoo
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