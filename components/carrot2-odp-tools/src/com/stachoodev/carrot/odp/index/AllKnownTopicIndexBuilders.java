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
package com.stachoodev.carrot.odp.index;

import java.util.*;

/**
 * Stores {@link com.stachoodev.carrot.odp.index.TopicIndexBuilder}s
 * corresponding to names of indices returned by
 * {@link com.stachoodev.carrot.odp.ODPIndex#getTopicIndexNames()}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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

        indexBuilders.put("path", new PathTopicIndexBuilder());
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
    
    /**
     * @return
     */
    public static Map getTopicIndexBuilders()
    {
        return indexBuilders;
    }
}