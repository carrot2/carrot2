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
public class LingoClusterLabel implements Comparable
{
    /** */
    private String image;

    /** */
    private int [] wordCodes;

    /** */
    private double score;

    /**
     * @param image
     * @param wordCodes
     */
    public LingoClusterLabel(String image, int [] wordCodes)
    {
        super();
        this.image = image;
        this.wordCodes = wordCodes;
    }

    /**
     * Returns this LingoClusterLabel's <code>image</code>.
     * 
     * @return
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Returns this LingoClusterLabel's <code>wordCodes</code>.
     * 
     * @return
     */
    public int [] getWordCodes()
    {
        return wordCodes;
    }

    /**
     * Returns this LingoClusterLabel's <code>score</code>.
     * 
     * @return
     */
    public double getScore()
    {
        return score;
    }

    /**
     * Sets this LingoClusterLabel's <code>score</code>.
     * 
     * @param score
     */
    public void setScore(double score)
    {
        this.score = score;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        LingoClusterLabel other = (LingoClusterLabel) o;

        if (getScore() < other.getScore())
        {
            return 1;
        }
        else if (getScore() > other.getScore())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return image;
    }
}