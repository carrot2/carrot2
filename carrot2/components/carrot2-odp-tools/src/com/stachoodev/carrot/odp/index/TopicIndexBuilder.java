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

import com.stachoodev.util.common.*;

/**
 * Defines the interface of an ODP topic index builder.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicIndexBuilder extends PropertyProvider
{
    /**
     * Creates a {@link TopicIndex}based on given
     * {@link PrimaryTopicIndex}.
     * 
     * @param primaryCategoryIndex
     * @return
     */
    public TopicIndex create(PrimaryTopicIndex primaryCategoryIndex);
}