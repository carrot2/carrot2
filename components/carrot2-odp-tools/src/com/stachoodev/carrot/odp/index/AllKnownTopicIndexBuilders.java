/*
 * AllKnownTopicIndexBuilders.java
 * 
 * Created on 2004-06-27
 */
package com.stachoodev.carrot.odp.index;

import java.util.*;

/**
 * Stores {@link com.stachoodev.carrot.odp.index.TopicIndexBuilder}s
 * corresponding to names of indices returned by
 * {@link com.stachoodev.carrot.odp.ODPIndex#getTopicIndexNames()}.
 * 
 * @author stachoo
 */
public class AllKnownTopicIndexBuilders
{
    /** Stores all known index builders */
    private static final Map indexBuilders;

    /** The PrimaryTopicIndexBuilder */
    private static final PrimaryTopicIndexBuilder primaryTopicIndexBuilder;

    /** Initialize the map */
    static
    {
        indexBuilders = new HashMap();
        primaryTopicIndexBuilder = new CatidPrimaryTopicIndexBuilder();

        // No extra indices defined yet
    }

    /** No instantiation */
    private AllKnownTopicIndexBuilders()
    {
    }

    /**
     * Returns the {@link TopicIndexBuilder}for given index name.
     * 
     * @param indexName
     * @return
     */
    public static TopicIndexBuilder getTopicIndexBuilder(String indexName)
    {
        return (TopicIndexBuilder) indexBuilders.get(indexName);
    }

    /**
     * Returns the {@link PrimaryTopicIndexBuilder}.
     * 
     * @return
     */
    public static PrimaryTopicIndexBuilder getPrimaryTopicIndexBuilder()
    {
        return primaryTopicIndexBuilder;
    }
}