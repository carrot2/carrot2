

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


/**
 * A generic interface for representing clusters of <code>Hit</code> objects.
 */
public interface Cluster
{
    /**
     * @return Returns an array of phrases describing the cluster. The phrases are ordered, i.e.
     *         the first one is the most relevant.
     */
    public String [] getNamePhrases();


    /**
     * @return Returns an <code>Iterator</code> of sub-clusters of this object or <code>null</code>
     *         if no sub-clusters exist.
     */
    public java.util.Iterator getSubClusters();


    /**
     * @return Returns an Iterator of <code>Hit</code> objects this cluster contains (ordered
     *         according to relevance to the cluster). May return <code>null</code> when the
     *         cluster only contains subclusters.
     */
    public java.util.Iterator getHits();
}
