
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

package org.carrot2.filter.lingo.util.suffixarrays;

import org.carrot2.filter.lingo.util.suffixarrays.wrapper.IntWrapper;


/**
 *
 */
public class DummyLcpSuffixSorter extends AbstractLcpSuffixSorter
    implements LcpSuffixSorter {
    /**
     *
     */
    public LcpSuffixArray lcpSuffixSort(IntWrapper intWrapper) {
        int[] suffixData = intWrapper.asIntArray();
        int[] suffixArray = new int[suffixData.length - 1];
        int[] lcpArray = new int[suffixData.length];

        dummySuffixSort(suffixArray, suffixData);
        dummyLcp(suffixArray, lcpArray, suffixData);

        return new LcpSuffixArray(suffixArray, lcpArray);
    }

    /**
     *
     */
    public SuffixArray suffixSort(IntWrapper intWrapper) {
        return new SuffixArray(lcpSuffixSort(intWrapper).getSuffixArray());
    }

    /* ------------------------------------------------------------------ protected section */
    /* ------------------------------------------------------------------ private methods */

    /**
     * An O(N^2logN) computational complexity algorithm (average case).
     */
    private void dummySuffixSort(int[] suffixArray, int[] suffixData) {
        /**
         * Initial unsorted suffixArray
         */
        for (int i = 0; i < suffixArray.length; i++) {
            suffixArray[i] = i;
        }

        /**
         * Quick sort
         */
        suffixQuickSort(suffixArray, suffixData, 0, suffixArray.length - 1);
    }

    /**
     * Quick sort on suffix array
     */
    private void suffixQuickSort(int[] suffixArray, int[] suffixData, int left,
        int right) {
        if ((right - left) < 2) {
            return;
        }

        int centerSuffix = suffixArray[(left + right) / 2];
        int l = left;
        int r = right;

        while ((r - l) > 0) {
            while ((r >= left) &&
                    (compareSuffixes(suffixArray[r], centerSuffix, suffixData) > 0)) {
                r--;
            }

            while ((l <= right) &&
                    (compareSuffixes(suffixArray[l], centerSuffix, suffixData) < 0)) {
                l++;
            }

            if ((r - l) > 0) {
                int temp = suffixArray[l];
                suffixArray[l] = suffixArray[r];
                suffixArray[r] = temp;
            }
        }

        suffixQuickSort(suffixArray, suffixData, left, r);
        suffixQuickSort(suffixArray, suffixData, l, right);
    }

    /**
     * An O(N^2) computational complexity algorithm.
     */
    private void dummyLcp(int[] suffixArray, int[] lcpArray, int[] suffixData) {
        lcpArray[0] = 0;

        for (int i = 1; i < suffixArray.length; i++) {
            lcpArray[i] = 0;

            int j = 0;

            while (suffixData[suffixArray[i] + j] == suffixData[suffixArray[i -
                    1] + j]) {
                lcpArray[i]++;
                j++;
            }
        }
    }
}
