

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.adapters.localfilter;


import java.util.Iterator;


/**
 * Adapter for a clustering filter.
 */
public interface ClusteringFilterAdapter
{
    /**
     * Sets a named parameter sent to the filter component as part of the query.
     */
    public void setParameter(String name, String value);


    /**
     * Clusters a sequence of <code>Hit</code> objects and returns an iterator of
     * <code>Cluster</code> objects at the top level of the hierarchy of clusters.
     *
     * @param hits An iterator over the hit list.
     * @param query Query provides hints for the clustering filter. It may be null, but if it
     *        exists, it will increase the quality of clusters.
     *
     * @throws RuntimeException In case of problems.
     */
    public Iterator clusterHits(Iterator hits, String query)
        throws RuntimeException;
    ;
}
