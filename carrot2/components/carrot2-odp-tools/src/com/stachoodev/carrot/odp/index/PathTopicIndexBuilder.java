/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp.index;

import com.stachoodev.carrot.odp.*;

/**
 * Builds the path-based ODP index.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PathTopicIndexBuilder implements TopicIndexBuilder
{
    /** */
    private PathTopicIndex index;

    /**
     *  
     */
    public PathTopicIndexBuilder()
    {
        index = new PathTopicIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.TopicIndexBuilder#index(com.stachoodev.carrot.odp.Topic)
     */
    public void index(Topic topic)
    {
        index.add(topic.getId(), topic.getCatid());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.TopicIndexBuilder#getTopicIndex()
     */
    public TopicIndex getTopicIndex()
    {
        return index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.TopicIndexBuilder#initialize()
     */
    public void initialize()
    {
    }

}