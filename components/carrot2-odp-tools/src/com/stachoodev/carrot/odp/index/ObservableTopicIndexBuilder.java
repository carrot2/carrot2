/*
 * ObservableTopicIndexBuilder.java
 * 
 * Created on 2004-06-27
 */
package com.stachoodev.carrot.odp.index;

/**
 * Enables the index builder to provide information about the progress of
 * indexing.
 * 
 * @author stachoo
 */
public interface ObservableTopicIndexBuilder
{
    /**
     * Adds a {@link TopicIndexBuilderListener}.
     * 
     * @param listener
     */
    public abstract void addTopicIndexBuilderListener(
        TopicIndexBuilderListener listener);

    /**
     * Removes a {@link TopicIndexBuilderListener}.
     * 
     * @param listener
     */
    public abstract void removeTopicIndexBuilderListener(
        TopicIndexBuilderListener listener);
}