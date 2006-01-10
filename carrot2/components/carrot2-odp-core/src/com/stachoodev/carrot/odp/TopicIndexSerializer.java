
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
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