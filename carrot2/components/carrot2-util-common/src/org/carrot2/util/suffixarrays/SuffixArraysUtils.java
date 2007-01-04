
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

import org.carrot2.util.suffixarrays.wrapper.TypeAwareIntWrapper;

/**
 * This class provides a number of common operations on suffix arrays.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SuffixArraysUtils
{
    /**
     * Compares two suffixes of a string.
     * 
     * @param suffixA start position of the first suffix
     * @param suffixB start position of the second suffix
     * @param suffixData suffix data terminated with a -1 value.
     */
    public static int compareSuffixes(int suffixA, int suffixB,
        int [] suffixData)
    {
        // Reflexiveness
        if (suffixA == suffixB)
        {
            return 0;
        }

        while (suffixData[suffixA] == suffixData[suffixB])
        {
            suffixA++;
            suffixB++;
        }

        if (suffixData[suffixA] > suffixData[suffixB])
        {
            return 1;
        }
        else if (suffixData[suffixA] < suffixData[suffixB])
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * @param code
     * @param mask
     */
    private final static int maskCode(int code, int mask)
    {
        if (code >= 0)
        {
            return code & mask;
        }
        else
        {
            return code;
        }
    }

    /**
     * Compares two suffixes of a string.
     * 
     * @param suffixA start position of the first suffix
     * @param suffixB start position of the second suffix
     * @param suffixData suffix data terminated with a -1 value.
     */
    public static int compareSuffixes(int suffixA, int suffixB,
        int [] suffixData, int secondaryBits)
    {
        // Reflexiveness
        if (suffixA == suffixB)
        {
            return 0;
        }

        int mask = ~((1 << secondaryBits) - 1);

        // TODO: the suffixData[suffixX] > 0 condition should
        // actually be implemented by the MaskedIntWrapper interface
        // in a method e.g. getMaskedIntCode()
        while (maskCode(suffixData[suffixA], mask) == maskCode(
            suffixData[suffixB], mask))
        {
            suffixA++;
            suffixB++;
        }

        if (maskCode(suffixData[suffixA], mask) > maskCode(suffixData[suffixB],
            mask))
        {
            return 1;
        }
        else if (maskCode(suffixData[suffixA], mask) < maskCode(
            suffixData[suffixB], mask))
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Compares two suffixes of a string.
     * 
     * @param suffixA start position of the first suffix
     * @param suffixB start position of the second suffix
     * @param suffixData suffix data terminated with a -1 value.
     */
    public static int compareSuffixes(int suffixA, int suffixB,
        int length, int [] suffixData, int secondaryBits)
    {
        // Reflexiveness
        if (suffixA == suffixB)
        {
            return 0;
        }

        int mask = ~((1 << secondaryBits) - 1);

        // TODO: the suffixData[suffixX] > 0 condition should
        // actually be implemented by the MaskedIntWrapper interface
        // in a method e.g. getMaskedIntCode()
        while (--length > 0 && maskCode(suffixData[suffixA], mask) == maskCode(
            suffixData[suffixB], mask))
        {
            suffixA++;
            suffixB++;
        }

        if (maskCode(suffixData[suffixA], mask) > maskCode(suffixData[suffixB],
            mask))
        {
            return 1;
        }
        else if (maskCode(suffixData[suffixA], mask) < maskCode(
            suffixData[suffixB], mask))
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * An O(N^2logN) computational complexity algorithm (average case).
     */
    public static void qsSuffixSort(int [] suffixArray, int [] suffixData)
    {
        qsSuffixSort(suffixArray, suffixData, 0);
    }

    /**
     * An O(N^2logN) computational complexity algorithm (average case).
     */
    public static void qsSuffixSort(int [] suffixArray, int [] suffixData,
        int secondaryBits)
    {
        /**
         * Initial unsorted suffixArray
         */
        for (int i = 0; i < suffixArray.length; i++)
        {
            suffixArray[i] = i;
        }

        /**
         * Quick sort
         */
        suffixQuickSort(suffixArray, suffixData, 0, suffixArray.length - 1,
            secondaryBits);
    }

    /**
     * Quick sort on suffix array
     * 
     * @param secondaryBits
     */
    private static void suffixQuickSort(int [] suffixArray, int [] suffixData,
        int left, int right, int secondaryBits)
    {
        if ((right - left) < 2)
        {
            return;
        }

        int centerSuffix = suffixArray[(left + right) / 2];
        int l = left;
        int r = right;

        while ((r - l) > 0)
        {
            while ((r >= left)
                && (SuffixArraysUtils.compareSuffixes(suffixArray[r],
                    centerSuffix, suffixData, secondaryBits) > 0))
            {
                r--;
            }

            while ((l <= right)
                && (SuffixArraysUtils.compareSuffixes(suffixArray[l],
                    centerSuffix, suffixData, secondaryBits) < 0))
            {
                l++;
            }

            if ((r - l) > 0)
            {
                int temp = suffixArray[l];
                suffixArray[l] = suffixArray[r];
                suffixArray[r] = temp;
            }
        }

        suffixQuickSort(suffixArray, suffixData, left, r, secondaryBits);
        suffixQuickSort(suffixArray, suffixData, l, right, secondaryBits);
    }

    /**
     * An O(N^2) computational complexity algorithm.
     */
    public static void naiveLcp(int [] suffixArray, int [] lcpArray,
        int [] suffixData)
    {
        naiveLcp(suffixArray, lcpArray, suffixData, 0);
    }

    /**
     * An O(N^2) computational complexity algorithm.
     */
    public static void naiveLcp(int [] suffixArray, int [] lcpArray,
        int [] suffixData, int secondaryBits)
    {
        naiveLcp(suffixArray, lcpArray, suffixData, secondaryBits, null);
    }

    /**
     * An O(N^2) computational complexity algorithm.
     */
    public static void naiveLcp(int [] suffixArray, int [] lcpArray,
        int [] suffixData, int secondaryBits, TypeAwareIntWrapper wrapper)
    {
        int mask = ~((1 << secondaryBits) - 1);

        lcpArray[0] = 0;

        for (int i = 1; i < suffixArray.length; i++)
        {
            lcpArray[i] = 0;

            int j = 0;

            while ((suffixData[suffixArray[i] + j] & mask) == (suffixData[suffixArray[i - 1]
                + j] & mask))
            {
                lcpArray[i]++;
                j++;
            }

            // If we have a type-aware int wrapper we can strip the LCP off the
            // leading/ trailing stop words. This will nicely handle skipping
            // these phrases in the frequent phrase discovery phase.
            // NB: stop words can potentially be skipped selectively - e.g.
            // 'the' can be left as a leading stop word, but not as a trailing
            // word
            if (wrapper != null && lcpArray[i] > 0)
            {
                // Leading stop words - just set the LCP to 0
                if (wrapper.isStopWord(suffixData[suffixArray[i]]))
                {
                    lcpArray[i] = 0;
                    continue;
                }

                // Trailing stop words
                while (lcpArray[i] > 0
                    && wrapper.isStopWord(suffixData[suffixArray[i]
                        + lcpArray[i] - 1]))
                {
                    lcpArray[i]--;
                }
            }
        }
    }
}