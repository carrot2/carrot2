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
 * An interface for listening to events related to creating ODP indices.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicIndexBuilderListener
{
    /**
     * This method will be called every time a topic has been indexed.
     * 
     * @param 
     */
    public void topicIndexed();
}