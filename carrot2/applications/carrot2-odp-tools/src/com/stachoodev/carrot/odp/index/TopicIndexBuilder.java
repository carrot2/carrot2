
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

package com.stachoodev.carrot.odp.index;

import com.stachoodev.carrot.odp.*;

/**
 * Defines the interface of an ODP topic index builder.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicIndexBuilder
{
    /**
     * Called before any topics are added to the index.
     */
    public void initialize();

    /**
     * Adds a topic to the index.
     * 
     * @param topic to be added
     */
    public void index(Topic topic);

    /**
     * Returns the final index, called after all topics have been added to the
     * index.
     * 
     */
    public TopicIndex getTopicIndex();
}