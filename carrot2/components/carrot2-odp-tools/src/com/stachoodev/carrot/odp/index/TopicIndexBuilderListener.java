/*
 * TopicIndexBuilderListener.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp.index;

/**
 * An interface for listening to events related to creating ODP indices.
 * 
 * @author stachoo
 */
public interface TopicIndexBuilderListener
{
    /**
     * This method will be called every time a topic has been indexed.
     * 
     * @param 
     */
    public void topicIndexed();
}