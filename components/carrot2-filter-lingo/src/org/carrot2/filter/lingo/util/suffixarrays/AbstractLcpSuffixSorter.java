
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.util.suffixarrays;

import org.carrot2.filter.lingo.util.suffixarrays.wrapper.IntWrapper;


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
