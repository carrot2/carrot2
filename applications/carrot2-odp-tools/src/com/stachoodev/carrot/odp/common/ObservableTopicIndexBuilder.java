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
package com.stachoodev.carrot.odp.common;


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