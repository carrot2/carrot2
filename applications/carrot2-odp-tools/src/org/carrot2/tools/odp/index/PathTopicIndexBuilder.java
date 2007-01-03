
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.tools.odp.index;

import org.carrot2.input.odp.Topic;
import org.carrot2.input.odp.index.PathTopicIndex;
import org.carrot2.input.odp.index.TopicIndex;

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
     */
    public void index(Topic topic)
    {
        index.add(topic.getId(), topic.getCatid());
    }

    /*
     * (non-Javadoc)
     */
    public TopicIndex getTopicIndex()
    {
        return index;
    }

    /*
     * (non-Javadoc)
     */
    public void initialize()
    {
    }

}