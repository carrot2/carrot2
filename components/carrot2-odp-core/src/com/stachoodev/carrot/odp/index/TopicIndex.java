/*
 * TopicIndex.java Created on 2004-06-25
 */
package com.stachoodev.carrot.odp.index;

import java.util.*;

/**
 * Defines the interface of an ODP topic index.
 * 
 * @author stachoo
 */
public interface TopicIndex
{
    /**
     * Returns a list of {@link String}s denoting relative locations of files
     * containing topics specified in the query. If no locations have been
     * identified for given query, <code>null</code> will be returned.
     * 
     * @param query
     * @return
     */
    public List getLocations(Object query);
}