
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.suffixarrays;

/**
 * A LCP-enabled (Longest Common Prefix) suffix array.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LcpSuffixArray
{

    /** Suffix Array */
    protected int [] suffixArray;

    /** Longest Common Prefix array */
    protected int [] lcpArray;

    /**
     * Creates an LCP-enabled suffix array.
     * 
     * @param suffixArray
     * @param lcpArray
     */
    public LcpSuffixArray(int [] suffixArray, int [] lcpArray)
    {
        this.suffixArray = suffixArray;
        this.lcpArray = lcpArray;
    }

    /**
     * Returns the suffix array.
     * 
     * @return
     */
    public int [] getSuffixArray()
    {
        return suffixArray;
    }

    /**
     * Returns the LCP array.
     * 
     * @return
     */
    public int [] getLcpArray()
    {
        return lcpArray;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer("[ ");

        for (int i = 0; i < suffixArray.length; i++)
        {
            stringBuffer.append(Integer.toString(suffixArray[i]));
            stringBuffer.append(" ");
        }

        stringBuffer.append("]");

        return stringBuffer.toString();
    }
}