
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.sorting.IndirectComparator;
import com.carrotsearch.hppc.sorting.IndirectSort;

/**
 * A simple suffix sorting utility based on the generic sorting routines from {@link IndirectSort}.
 */
final class SuffixSorter
{
    /**
     * An int comparator that enables suffix sorting.
     */
    private static class SuffixComparator implements IndirectComparator
    {
        private int [] suffixData;

        public SuffixComparator(int [] suffixData)
        {
            this.suffixData = suffixData;
        }

        public int compare(int suffixA, int suffixB)
        {
            if (suffixA == suffixB)
            {
                return 0;
            }

            /*
             * Suffix data ends with a unique negative value, so we don't need to do extra
             * range checks and we still won't run into array index out of bounds
             * exceptions.
             */
            while (suffixData[suffixA] == suffixData[suffixB])
            {
                suffixA++;
                suffixB++;
            }

            return suffixData[suffixB] - suffixData[suffixA];
        }
    }

    /**
     * Performs suffix sorting and saves the results to the <code>context</code>.
     */
    void suffixSort(PreprocessingContext context)
    {
        /*
         * Create a temporary array based on word indices with -1 values replaced with
         * unique negative values. This will ensure that the phrases discovered based on
         * the sorted/lcp array will not cross sentence/field boundaries. At some point we
         * may want to make it an option. In this case, we'll need to review Substring and
         * SubstringComparator for possible array index out of bounds.
         */
        final int [] intCodes = new int [context.allTokens.wordIndex.length];
        System.arraycopy(context.allTokens.wordIndex, 0, intCodes, 0, intCodes.length);
        int currentSeparatorCode = -1;
        for (int i = 0; i < intCodes.length; i++)
        {
            if (intCodes[i] < 0)
            {
                intCodes[i] = currentSeparatorCode--;
            }
        }

        // Create suffix order
        int [] suffixOrder = IndirectSort.mergesort(0, intCodes.length, new SuffixComparator(intCodes));
        context.allTokens.suffixOrder = suffixOrder;
        
        // Add LCPs
        context.allTokens.lcp = calculateLcp(intCodes, suffixOrder);
    }

    /**
     * Calculates the Longest Common Prefix values for each token.
     */
    private int [] calculateLcp(int [] intCodes, int [] suffixOrder)
    {
        // LCP array
        int [] lcpArray = new int [intCodes.length];

        lcpArray[0] = 0;
        for (int i = 1; i < lcpArray.length - 1; i++)
        {
            int lcp = 0;
            while (intCodes[suffixOrder[i - 1] + lcp] == intCodes[suffixOrder[i] + lcp])
            {
                lcp++;
            }
            lcpArray[i] = lcp;
        }

        return lcpArray;
    }
}
