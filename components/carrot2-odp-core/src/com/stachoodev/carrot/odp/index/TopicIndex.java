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

import java.util.*;

/**
 * Defines the interface of an ODP topic index.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicIndex
{
    /**
     * Returns a list of {@link String}s denoting relative locations of files
     * containing topics specified in the query. If no locations have been
     * identified for given query, a non- <code>null</code> empty list must be
     * returned.
     * 
     * @param query
     * @return
     */
    public List getLocations(Object query);
}