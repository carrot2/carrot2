
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import org.apache.commons.lang.StringUtils;

/**
 * Class representing a word after being stemmed.
 */
public class StemmedTerm
{
    /** original word */
    public final String word;

    /** Stemmed form */
    public final String stemmed;

    /** A flag indicating whether this term is on the stop word list. */
    public final boolean stopword;

    /**
     * 
     */
    public StemmedTerm(String word, String stemmed, boolean stopword)
    {
        this.word = word;
        this.stemmed = (StringUtils.isEmpty(stemmed) ? word : stemmed);
        this.stopword = stopword;
    }

    /**
     * Indicates whether this term exists in the stop-list
     */
    public boolean isStopWord()
    {
        return stopword;
    }

    /**
     * Overrides hashCode() method of Object class
     */
    public int hashCode()
    {
        return stemmed.hashCode();
    }

    /**
     * Overrides equals() method of Object class
     */
    public boolean equals(Object other)
    {
        if (other instanceof StemmedTerm)
        {
            return stemmed.equals(((StemmedTerm) other).stemmed);
        }
        else
        {
            return false;
        }
    }

    /**
     * Implementation of {@link Comparable} interface
     */
    public int compareTo(Object p)
    {
        return stemmed.compareTo(((StemmedTerm) p).stemmed);
    }

    /**
     * Returns string representation of this object
     */
    public String toString()
    {
        return "{" + (this.stopword ? "!" : "") + this.word + "->" + stemmed + "}";
    }

    /**
     * Implementation of StemmedTerm interface
     */
    public String getTerm()
    {
        return this.word;
    }

    /**
     * Returns steemed form of original word
     */
    public String getStemmed()
    {
        return this.stemmed;
    }
}
