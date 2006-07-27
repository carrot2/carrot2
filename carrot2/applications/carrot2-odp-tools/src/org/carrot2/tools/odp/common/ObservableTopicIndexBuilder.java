
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

package org.carrot2.tools.odp.common;


/**
 * Enables the index builder to provide information about the progress of
 * indexing.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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