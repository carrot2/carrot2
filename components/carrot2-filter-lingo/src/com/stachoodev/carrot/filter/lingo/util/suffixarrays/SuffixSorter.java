
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

package com.stachoodev.carrot.filter.lingo.util.suffixarrays;

import com.stachoodev.carrot.filter.lingo.util.suffixarrays.wrapper.IntWrapper;


/**
 * Defines the functionality of a suffix sorting algorithm.
 */
public interface SuffixSorter {
    /**
     * Soffix-sorts given input.
     *
     * @param intWrapper integer representation of the input to be sorted
     *
     * @return SuffixArray suffix-sorted input data
     */
    public SuffixArray suffixSort(IntWrapper intWrapper);
}
