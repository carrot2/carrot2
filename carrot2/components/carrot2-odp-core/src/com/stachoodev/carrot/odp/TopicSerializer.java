/*
 * TopicSerializer.java
 * 
 * Created on 2004-06-25
 */
package com.stachoodev.carrot.odp;

import java.io.*;

/**
 * Defines the interface for serializing/deserializing ODP topics.
 * 
 * @author stachoo
 */
public interface TopicSerializer
{
    /**
     * @param topic
     * @param location
     * @throws IOException
     */
    public void serialize(Topic topic, String location) throws IOException;

    /**
     * @param location
     * @return @throws IOException
     * @throws ClassNotFoundException
     */
    public Topic deserialize(String location) throws IOException,
        ClassNotFoundException;
}