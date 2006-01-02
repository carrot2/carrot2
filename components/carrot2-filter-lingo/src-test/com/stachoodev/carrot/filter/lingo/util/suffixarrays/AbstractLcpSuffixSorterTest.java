
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.util.suffixarrays;

import junit.framework.TestCase;


/**
 *
 */
public class AbstractLcpSuffixSorterTest extends TestCase {
    /**
     * Suffix comparisons tests.
     */
    public void testSuffixComparator() {
        int[] suffixData = new int[] { 1, 2, 1, 3, 2, 3, 1, 2, -1 };

        assertEquals("Reflexive comparison: ",
            AbstractLcpSuffixSorter.compareSuffixes(0, 0, suffixData), 0);

        assertEquals("Alternation: ",
            AbstractLcpSuffixSorter.compareSuffixes(3, 2, suffixData),
            -AbstractLcpSuffixSorter.compareSuffixes(2, 3, suffixData));

        assertEquals("Comparison #1: ",
            AbstractLcpSuffixSorter.compareSuffixes(4, 7, suffixData), 1);

        assertEquals("Comparison #2: ",
            AbstractLcpSuffixSorter.compareSuffixes(0, 6, suffixData), 1);

        assertEquals("Comparison #3: ",
            AbstractLcpSuffixSorter.compareSuffixes(2, 5, suffixData), -1);
    }
}
