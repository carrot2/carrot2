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

import com.stachoodev.carrot.odp.index.*;

/**
 * Defines the interface of ODP topic index serializer/deserializer.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicIndexSerializer
{
    /**
     * Serializes given topicIndex to given location
     * 
     * @param topicIndex
     * @param location
     * @throws IOException
     */
    public void serialize(TopicIndex topicIndex, String location)
        throws IOException;

    /**
     * Deserializes a {@link TopicIndex}from given location.
     * 
     * @param location
     * @return @throws IOException
     * @throws ClassNotFoundExceptionClassNotFoundException
     */
    public TopicIndex deserialize(String location) throws IOException,
        ClassNotFoundException;
}