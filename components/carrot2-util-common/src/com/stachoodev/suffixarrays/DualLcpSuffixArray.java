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
package com.stachoodev.suffixarrays;

/**
 * A suffix array that has two Longest Common Prefix arrays. Apart from the
 * usual LCP array, it has a super LCP array, which describes generalized
 * prefixes of the string.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class DualLcpSuffixArray extends LcpSuffixArray
{
    /** Super LCP array. Contains generalized LCPs */
    protected int [] superLcpArray;

    /**
     * @param suffixArray
     * @param lcpArray
     */
    public DualLcpSuffixArray(int [] suffixArray, int [] lcpArray,
        int [] secondaryLcpArray)
    {
        super(suffixArray, lcpArray);

        this.superLcpArray = secondaryLcpArray;
    }

    /**
     * Returns the super LCP array.
     * 
     * @return
     */
    public int [] getSuperLcpArray()
    {
        return superLcpArray;
    }
}