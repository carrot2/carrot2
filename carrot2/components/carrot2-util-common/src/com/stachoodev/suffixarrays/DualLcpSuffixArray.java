
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.suffixarrays;

/**
 * A suffix array that has two suffix arrays and two Longest Common Prefix
 * arrays. Apart from the usual suffix and LCP arrays, it has a super-versions
 * of these, which are generated for the generalised versions of the symbols.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class DualLcpSuffixArray extends LcpSuffixArray
{
    /** Super LCP array. Contains generalized LCPs */
    protected int [] superLcpArray;

    /**
     * Super Suffix array. Contains suffixes sorted according to the generalized
     * symbol sequences
     */
    protected int [] superSuffixArray;

    /**
     * @param suffixArray
     * @param lcpArray
     */
    public DualLcpSuffixArray(int [] suffixArray, int [] lcpArray,
        int [] superSuffixArray, int [] superLcpArray)
    {
        super(suffixArray, lcpArray);

        this.superSuffixArray = superSuffixArray;
        this.superLcpArray = superLcpArray;
    }

    /**
     * Returns the super suffix array;
     * 
     * @return 
     */
    public int [] getSuperSuffixArray()
    {
        return superSuffixArray;
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