/*
 * Carrot2 Project Copyright (C) 2002-2004, Dawid Weiss Portions (C)
 * Contributors listed in carrot2.CONTRIBUTORS file. All rights reserved. Refer
 * to the full license file "carrot2.LICENSE" in the root folder of the CVS
 * checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.stachoodev.suffixarrays;

import com.stachoodev.suffixarrays.wrapper.*;

/**
 * Defines an interface of a suffix sorting algorithm, which also calculates the
 * primary and secondary Longest Common Prefix information.
 */
public interface DualLcpSuffixSortingStrategy
{
    /**
     * Based on the input IntWrapper returns a sorted suffix array plus primary
     * and secondary LCP information.
     * 
     * @param intWrapper
     * @return
     */
    public DualLcpSuffixArray dualLcpSuffixSort(MaskableIntWrapper intWrapper);
}