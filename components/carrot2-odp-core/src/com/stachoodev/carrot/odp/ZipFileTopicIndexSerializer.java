/*
 * ZipFileTopicIndexSerializer.java
 * 
 * Created on 2004-06-27
 */
package com.stachoodev.carrot.odp;

import java.io.*;
import java.util.zip.*;

import com.stachoodev.carrot.odp.index.*;

/**
 * @author stachoo
 */
public class ZipFileTopicIndexSerializer implements TopicIndexSerializer
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.TopicIndexSerializer#serialize(com.stachoodev.carrot.odp.index.TopicIndex,
     *      java.lang.String)
     */
    public void serialize(TopicIndex topicIndex, String location)
        throws IOException
    {
        // Create directories
        File file = new File(location);
        file.getParentFile().mkdirs();

        ZipOutputStream zipOutputStream = new ZipOutputStream(
            new FileOutputStream(file));
        zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
        zipOutputStream.putNextEntry(new ZipEntry("index"));
        ObjectOutputStream stream = new ObjectOutputStream(zipOutputStream);

        stream.writeObject(topicIndex);

        stream.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.TopicIndexSerializer#deserialize(java.lang.String)
     */
    public TopicIndex deserialize(String location) throws IOException,
        ClassNotFoundException
    {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(
            location));
        
        zipInputStream.getNextEntry();
        ObjectInputStream input = new ObjectInputStream(zipInputStream);
        TopicIndex topicIndex = (TopicIndex) input.readObject();

        input.close();

        return topicIndex;
    }
}