/*
 * Carrot2 Project Copyright (C) 2002-2004, Dawid Weiss Portions (C)
 * Contributors listed in carrot2.CONTRIBUTORS file. All rights reserved. Refer
 * to the full license file "carrot2.LICENSE" in the root folder of the CVS
 * checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.stachoodev.suffixarrays;

import junit.framework.TestCase;

/**
 *
 */
public class SuffixArraysUtilsTest extends TestCase
{

    /**
     * Suffix comparisons tests.
     */
    public void testSuffixComparator()
    {
        int [] suffixData = new int[]
        { 1, 2, 1, 3, 2, 3, 1, 2, -1 };

        assertEquals("Reflexive comparison: ", SuffixArraysUtils
                .compareSuffixes(0, 0, suffixData), 0);

        assertEquals("Alternation: ", SuffixArraysUtils.compareSuffixes(3, 2,
                suffixData), -SuffixArraysUtils.compareSuffixes(2, 3,
                suffixData));

        assertEquals("Comparison #1: ", SuffixArraysUtils.compareSuffixes(4, 7,
                suffixData), 1);

        assertEquals("Comparison #2: ", SuffixArraysUtils.compareSuffixes(0, 6,
                suffixData), 1);

        assertEquals("Comparison #3: ", SuffixArraysUtils.compareSuffixes(2, 5,
                suffixData), -1);
    }
}