
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

package org.carrot2.tools.odp.index;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores {@link com.stachoodev.carrot.odp.index.TopicIndexBuilder}s.
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
     */
    public static TopicIndexBuilder getTopicIndexBuilder(String indexName)
    {
        return (TopicIndexBuilder) indexBuilders.get(indexName);
    }

    /**
     * Returns the {@link PrimaryTopicIndexBuilder}.
     * 
     */
    public static PrimaryTopicIndexBuilder getPrimaryTopicIndexBuilder()
    {
        return primaryTopicIndexBuilder;
    }
    
    /**
     */
    public static Map getTopicIndexBuilders()
    {
        return indexBuilders;
    }
}