/*
 * TopicIndexSerializer.java
 * 
 * Created on 2004-06-27
 */
package com.stachoodev.carrot.odp;

import java.io.*;

import com.stachoodev.carrot.odp.index.*;

/**
 * Defines the interface of ODP topic index serializer/deserializer.
 * 
 * @author stachoo
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