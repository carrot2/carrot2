/*
 * TopicIndexBuilder.java Created on 2004-06-25
 */
package com.stachoodev.carrot.odp.index;

/**
 * Defines the interface of an ODP topic index builder.
 * 
 * @author stachoo
 */
public interface TopicIndexBuilder
{
    /**
     * Creates a {@link TopicIndex}based on given
     * {@link PrimaryTopicIndex}.
     * 
     * @param primaryCategoryIndex
     * @return
     */
    public TopicIndex create(PrimaryTopicIndex primaryCategoryIndex);
}