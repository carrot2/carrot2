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

import com.stachoodev.carrot.odp.index.*;

/**
 * Defines the interface for serializing/deserializing ODP topics.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicSerializer
{
    /**
     * Initializes the serializer.
     * 
     * @param location location of the data files.
     * @throws IOException
     */
    public void initialize(String dataLocation) throws IOException;

    /**
     * Disposes the serializer. At this point e.g. data files can be closed.
     * 
     * @throws IOException
     */
    public void dispose() throws IOException;

    /**
     * Serializes a single topic.
     * 
     * @param topic topic to be serialized
     * @return location the index can used to retrieve the topic
     * @throws IOException
     */
    public Location serialize(Topic topic) throws IOException;

    /**
     * Deserializes a single topic.
     * 
     * @param location location from which the topic is to be deserialized
     * @return deserialized topic
     * @throws IOException
     */
    public Topic deserialize(Location location) throws IOException;
}