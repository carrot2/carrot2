/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp;

import java.io.*;
import java.util.*;

import com.stachoodev.carrot.odp.index.*;

/**
 * This singleton provides centralized access to the ODP indexing
 * infrastructure. Use this class to discover existing indices, retrieve topic
 * locations and topic contents. Note: this class is not guaranteed to be
 * thread-safe.
 * 
 * TODO: refactor to a factory?
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ODPIndex
{
    /** An instance of TopicSerializer */
    private static final TopicSerializer topicSerializer;
    private static final LocationFactory locationFactory;

    /** The PrimaryTopicIndex */
    private static PrimaryTopicIndex primaryTopicIndex = new SimplePrimaryTopicIndex();
    public static final String PRIMARY_TOPIC_INDEX_NAME = "primary";

    /** A map of all available topic indices (values) arranged by name (key) */
    private static final Map topicIndices;

    /** */
    private static final Map uninitializedTopicIndices;

    /** Location of all the data */
    private static String dataLocation;

    /** Initialize some stuff here */
    static
    {
        RandomAccessTopicSerializer randomAccessTopicSerializer = new RandomAccessTopicSerializer(
            (byte) 36);
        topicSerializer = randomAccessTopicSerializer;
        locationFactory = randomAccessTopicSerializer;
        topicIndices = new HashMap();

        uninitializedTopicIndices = new HashMap();
        uninitializedTopicIndices.put("path", new PathTopicIndex());
    }

    /**
     * No public constructor.
     */
    private ODPIndex()
    {
    }

    /**
     * Initializes this singleton. Note that this method must be called before
     * any indices are retrieved. It can not be called more than once, uness the
     * index has been disposed of using the {@link #dispose()}method.
     * 
     * @param indexDataLocation
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static synchronized void initialize(String indexDataLocation)
        throws IOException
    {
        if (dataLocation == null)
        {
            if (indexDataLocation == null)
            {
                throw new IllegalArgumentException(
                    "indexDataLocation must not be null");
            }

            topicSerializer.initialize(indexDataLocation);
            dataLocation = new File(indexDataLocation).getAbsolutePath();
            FileInputStream fileInputStream = new FileInputStream(dataLocation
                + System.getProperty("file.separator")
                + PRIMARY_TOPIC_INDEX_NAME);
            primaryTopicIndex.deserialize(fileInputStream, locationFactory);
            fileInputStream.close();
        }
        else
        {
            throw new RuntimeException("ODP index already initialized");
        }
    }

    /**
     * Disposes of the current ODP index. It can be initialized from another
     * file system location by the {@link #initialize(String)}method.
     * 
     * @throws IOException
     */
    public static void dispose() throws IOException
    {
        topicSerializer.dispose();
        dataLocation = null;
    }

    /**
     * Returns <code>true</code> when the ODPIndex has been initialized.
     * 
     * @return
     */
    public static boolean isInitialized()
    {
        return dataLocation != null;
    }

    /**
     * Returns the primary topic index. The ODPIndex must be initialized (see
     * {@link #initialize(String)}) before calling this method.
     * 
     * @return
     */
    public static PrimaryTopicIndex getPrimaryTopicIndex()
    {
        if (dataLocation == null)
        {
            throw new RuntimeException("ODPIndex not initialized");
        }

        return primaryTopicIndex;
    }

    /**
     * The ODPIndex must be initialized (see {@link #initialize(String)})
     * before calling this method.
     * 
     * @param name
     * @return
     */
    public static TopicIndex getTopicIndex(String name)
    {
        if (dataLocation == null)
        {
            throw new RuntimeException("ODPIndex not initialized");
        }

        synchronized (ODPIndex.class)
        {
            if (!topicIndices.containsKey(name))
            {
                TopicIndex topicIndex = null;
                try
                {
                    topicIndex = (TopicIndex) uninitializedTopicIndices
                        .get(name);
                    if (topicIndex != null)
                    {
                        FileInputStream fileInputStream = new FileInputStream(
                            dataLocation + System.getProperty("file.separator")
                                + name);
                        topicIndex.deserialize(fileInputStream);
                        fileInputStream.close();
                        System.gc();
                        System.gc();
                        System.gc();
                        System.gc();
                        System.gc();
                    }
                }
                catch (IOException ignored)
                {
                    // We will store null to indicate that the index is not
                    // available
                }
                topicIndices.put(name, topicIndex);
            }
        }

        return (TopicIndex) topicIndices.get(name);
    }

    /**
     * Returns the {@link TopicSerializer}that must be used to deserialize
     * topic objects from locations given by indices.
     * 
     * @return
     */
    public static TopicSerializer getTopicSerializer()
    {
        return topicSerializer;
    }

    /**
     * A convenience method to deserializes an ODP topic from given relative
     * location. In case of any problems
     * <code>null<code> will be returned. The ODPIndex must be initialized (see
     * {@link #initialize(String)}) before calling this method.
     * 
     * @param location
     * @return
     */
    public static Topic getTopic(Location location)
    {
        try
        {
            return topicSerializer.deserialize(location);
        }
        catch (IOException e)
        {
            return null;
        }
    }
}