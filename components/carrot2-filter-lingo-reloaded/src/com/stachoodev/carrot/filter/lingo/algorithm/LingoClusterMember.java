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
package com.stachoodev.carrot.filter.lingo.algorithm;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoClusterMember implements Comparable
{
    /** */
    private int documentIndex;

    /** */
    private double score;

    /**
     * @param documentIndex
     * @param score
     */
    public LingoClusterMember(int documentIndex, double score)
    {
        this.documentIndex = documentIndex;
        this.score = score;
    }

    /**
     * Returns this LingoClusterMember's <code>documentIndex</code>.
     * 
     * @return
     */
    public int getDocumentIndex()
    {
        return documentIndex;
    }

    /**
     * Returns this LingoClusterMember's <code>score</code>.
     * 
     * @return
     */
    public double getScore()
    {
        return score;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        LingoClusterMember other = (LingoClusterMember) o;

        if (score < other.score)
        {
            return 1;
        }
        else if (score > other.score)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}