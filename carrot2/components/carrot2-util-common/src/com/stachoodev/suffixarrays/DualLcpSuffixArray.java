/*
 * DualLcpSuffixArray.java Created on 2004-06-17
 */
package com.stachoodev.suffixarrays;

/**
 * A suffix array that has two Longest Common Prefix arrays. Apart from the
 * usual LCP array, it has a super LCP array, which describes generalized
 * prefixes of the string.
 * 
 * @author stachoo
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