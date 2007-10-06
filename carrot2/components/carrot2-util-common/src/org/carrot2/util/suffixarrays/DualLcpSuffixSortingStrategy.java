
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.suffixarrays;

import org.carrot2.util.suffixarrays.wrapper.MaskableIntWrapper;

/**
 * Defines an interface of a suffix sorting algorithm, which also calculates the
 * primary and secondary Longest Common Prefix information.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface DualLcpSuffixSortingStrategy
{
    /**
     * Based on the input IntWrapper returns a sorted suffix array plus primary
     * and secondary LCP information.
     * 
     * @param intWrapper
     */
    public DualLcpSuffixArray dualLcpSuffixSort(MaskableIntWrapper intWrapper);
}