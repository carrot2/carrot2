/*
 * CompressedTopicSerializer.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp;

import java.io.*;
import java.util.zip.*;

/**
 * Serializes ODP topics as ZIP compressed files.
 * 
 * @author stachoo
 */
public class CompressedTopicSerializer implements TopicSerializer
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
        ZipInputStream zipInputStream = new ZipInputStream(
            new FileInputStream(location));
        zipInputStream.getNextEntry();
        ObjectInputStream input = new ObjectInputStream(zipInputStream);

        Topic topic = (Topic) input.readObject();
        
        input.close();

        return topic;
    }
}