
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
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
 *
 */
public abstract class AbstractLcpSuffixSorter implements LcpSuffixSorter {
    /**
     *
     */
    public abstract LcpSuffixArray lcpSuffixSort(IntWrapper intWrapper);

    /**
     *
     */
    public abstract SuffixArray suffixSort(IntWrapper intWrapper);

    /* ------------------------------------------------------------------ protected section */
    /* ------------------------------------------------------------------ package section */
    /**
     *
     */
    static int compareSuffixes(int suffixA, int suffixB, int[] suffixData) {
        // Reflexiveness
        if (suffixA == suffixB) {
            return 0;
        }

        while ((suffixA < suffixData.length) && (suffixB < suffixData.length) &&
                (suffixData[suffixA] == suffixData[suffixB])) {
            suffixA++;
            suffixB++;
        }

        if (suffixData[suffixA] > suffixData[suffixB]) {
            return 1;
        } else if (suffixData[suffixA] < suffixData[suffixB]) {
            return -1;
        } else {
            return 0;
        }
    }

    /* ------------------------------------------------------------------ private methods */
}
