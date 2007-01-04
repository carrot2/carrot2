
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

package org.carrot2.tools.odp.common;

/**
 * An interface for listening to events related to creating ODP indices.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicIndexBuilderListener
{
    /**
     * This method will be called every time a topic has been indexed.
     */
    public void topicIndexed();
}