
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

import org.carrot2.util.suffixarrays.wrapper.IntWrapper;

/**
 * Defines an interface of a suffix sorting algorithm, which also calculates the
 * Longest Common Prefix information.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface LcpSuffixSortingStrategy
{

    /**
     * Based on the input IntWrapper returns a sorted suffix array plus LCP
     * information.
     * 
     * @param intWrapper
     */
    public LcpSuffixArray lcpSuffixSort(IntWrapper intWrapper);
}