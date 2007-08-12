
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.odp;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.carrot2.input.odp.index.*;

/**
 * A topic serialized based on random access files.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RandomAccessTopicSerializer implements TopicSerializer,
    LocationFactory
{
    /** The number of files we have */
    private byte dataFileCount;

    /** File handles */
    private RandomAccessFile [] files;

    /** Need this while serialization */
    private ByteArrayOutputStream byteArrayOutputStream;

    /**
     * Creates a new instance of the serializer.
     * 
     * @param dataFileCount the number of files to be used by the serializer
     */
    public RandomAccessTopicSerializer(byte dataFileCount)
    {
        this.dataFileCount = dataFileCount;
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.TopicSerializer#initialize(java.lang.String)
     */
    public void initialize(String dataLocation) throws IOException
    {
        // Create/open index files
        files = new RandomAccessFile [dataFileCount];
        for (int i = 0; i < files.length; i++)
        {
            files[i] = new RandomAccessFile(dataLocation + File.separator
                + Integer.toString(i) + ".dat", "rw");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.TopicSerializer#dispose()
     */
    public void dispose() throws IOException
    {
        for (int i = 0; i < files.length; i++)
        {
            files[i].close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.TopicSerializer#serialize(org.carrot2.input.odp.Topic,
     *      java.lang.String)
     */
    public Location serialize(Topic topic) throws IOException
    {
        byteArrayOutputStream.reset();

        OutputStream outputStream;
        boolean compression;

        // Compress topics that are bigger than some threshold
        if (topic.getExternalPages().size() >= 10)
        {
            outputStream = new GZIPOutputStream(byteArrayOutputStream);
            compression = true;
        }
        else
        {
            outputStream = byteArrayOutputStream;
            compression = false;
        }

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
            outputStream);
        objectOutputStream.writeUTF(topic.getId());
        objectOutputStream.writeInt(topic.getCatid());
        objectOutputStream.writeInt(topic.getExternalPages().size());
        List externalPages = topic.getExternalPages();
        for (Iterator iter = externalPages.iterator(); iter.hasNext();)
        {
            ExternalPage externalPage = (ExternalPage) iter.next();
            objectOutputStream.writeBoolean(externalPage.getTitle() != null);
            objectOutputStream
                .writeBoolean(externalPage.getDescription() != null);
            if (externalPage.getTitle() != null)
            {
                objectOutputStream.writeUTF(externalPage.getTitle());
            }
            if (externalPage.getDescription() != null)
            {
                objectOutputStream.writeUTF(externalPage.getDescription());
            }
        }
        objectOutputStream.close();

        byte fileIndex = (byte) (topic.getCatid() % dataFileCount);
        long position = files[fileIndex].getFilePointer();
        files[fileIndex].write(byteArrayOutputStream.toByteArray());

        return new RandomAccessLocation(fileIndex, position,
            byteArrayOutputStream.size(), compression);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.TopicSerializer#deserialize(java.lang.String)
     */
    public Topic deserialize(Location location) throws IOException
    {
        int fileIndex = ((RandomAccessLocation) location).fileIndex;
        long position = ((RandomAccessLocation) location).position;
        int length = ((RandomAccessLocation) location).size;
        boolean compression = ((RandomAccessLocation) location).compressed;

        // Read binary data
        files[fileIndex].seek(position);
        byte [] data = new byte [length];
        files[fileIndex].read(data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            data);
        ObjectInputStream objectInputStream;

        // Decompress if needed
        if (compression)
        {
            objectInputStream = new ObjectInputStream(new GZIPInputStream(
                byteArrayInputStream));
        }
        else
        {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
        }

        // Deserialize
        MutableTopic topic = new MutableTopic(objectInputStream.readUTF());
        topic.setCatid(objectInputStream.readInt());
        int externalPageCount = objectInputStream.readInt();
        for (int i = 0; i < externalPageCount; i++)
        {
            boolean hasTitle = objectInputStream.readBoolean();
            boolean hasDescription = objectInputStream.readBoolean();
            topic.addExternalPage(new MutableExternalPage(
                (hasTitle ? objectInputStream.readUTF() : null),
                (hasDescription ? objectInputStream.readUTF() : null)));
        }

        objectInputStream.close();

        return topic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.LocationFactory#createLocation()
     */
    public Location createLocation()
    {
        return new RandomAccessLocation();
    }

    /**
     * Implements index location to be used by the indices when using this 
     * serializer.
     */
    public static class RandomAccessLocation implements Location
    {
        /** Which random access file */
        private byte fileIndex;

        /** Position in the file */
        private long position;

        /** Size of the data block */
        private int size;

        /** Is the block compressed */
        private boolean compressed;

        /**
         * Creates a new location.
         */
        public RandomAccessLocation()
        {
        }

        /**
         * Creates a new location.
         * 
         * @param fileIndex
         * @param position
         * @param size
         * @param compressed
         */
        public RandomAccessLocation(byte fileIndex, long position, int size,
            boolean compressed)
        {
            this.fileIndex = fileIndex;
            this.position = position;
            this.size = size;
            this.compressed = compressed;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.carrot2.input.odp.index.Location#serialize(java.io.ObjectOutputStream)
         */
        public void serialize(ObjectOutputStream objectOutputStream)
            throws IOException
        {
            objectOutputStream.writeByte(fileIndex);
            objectOutputStream.writeLong(position);
            objectOutputStream.writeInt(size);
            objectOutputStream.writeBoolean(compressed);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.carrot2.input.odp.index.Location#deserialize(java.io.ObjectOutputStream)
         */
        public void deserialize(ObjectInputStream objectInputStream)
            throws IOException
        {
            fileIndex = objectInputStream.readByte();
            position = objectInputStream.readLong();
            size = objectInputStream.readInt();
            compressed = objectInputStream.readBoolean();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }

            if (obj == null)
            {
                return false;
            }

            if (obj.getClass() != getClass())
            {
                return false;
            }

            RandomAccessLocation other = (RandomAccessLocation) obj;

            return other.fileIndex == fileIndex && other.position == position
                && other.size == size && other.compressed == compressed;
        }
    }
}