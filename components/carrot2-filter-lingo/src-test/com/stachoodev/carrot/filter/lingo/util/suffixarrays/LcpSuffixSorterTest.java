
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
import com.stachoodev.carrot.filter.lingo.util.suffixarrays.wrapper.StringIntWrapper;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


/**
 *
 */
public class LcpSuffixSorterTest extends TestCase {
    /** */

    /** DOCUMENT ME! */
    private LcpSuffixSorter[] algorithms;

    /** */

    /** DOCUMENT ME! */
    private static final IntWrapper emptySuffixData = new StringIntWrapper("");

    /** DOCUMENT ME! */
    private static final int[] emptySuffixDataResult = new int[] {  };

    /** DOCUMENT ME! */
    private static final int[] emptySuffixDataLcp = new int[] { 0 };

    /** */

    /** DOCUMENT ME! */
    private static final IntWrapper suffixData = new StringIntWrapper(
            "to_be_or_not_to_be");

    /** DOCUMENT ME! */
    private static final int[] suffixDataResult = new int[] {
            15, 2, 8, 5, 12, 16, 3, 17, 4, 9, 14, 1, 6, 10, 7, 11, 13, 0
        };

    /** DOCUMENT ME! */
    private static final int[] suffixDataLcp = new int[] {
            0, 3, 1, 1, 1, 0, 2, 0, 1, 0, 0, 4, 1, 1, 0, 0, 1, 5, 0
        };

    /**
     *
     */
    public void setUp() {
        algorithms = new LcpSuffixSorter[] { new DummyLcpSuffixSorter() };
    }

    /**
     *
     */
    public void testSorting() {
        for (int i = 0; i < algorithms.length; i++) {
            assertEquals(algorithms[i].getClass().toString() + " - EmptyData",
                emptySuffixDataResult,
                algorithms[i].suffixSort(emptySuffixData).getSuffixArray());

            assertEquals(algorithms[i].getClass().toString() +
                " - NonemptyData", suffixDataResult,
                algorithms[i].suffixSort(suffixData).getSuffixArray());
        }
    }

    /**
     *
     */
    public void testLcp() {
        for (int i = 0; i < algorithms.length; i++) {
            assertEquals(algorithms[i].getClass().toString() + " - EmptyData",
                emptySuffixDataLcp,
                algorithms[i].lcpSuffixSort(emptySuffixData).getLcpArray());

            assertEquals(algorithms[i].getClass().toString() +
                " - NonemptyData", suffixDataLcp,
                algorithms[i].lcpSuffixSort(suffixData).getLcpArray());
        }
    }

    /**
     *
     */
    protected void assertEquals(String comment, int[] arrayA, int[] arrayB) {
        assertEquals(comment + " - arrayLengths:", arrayA.length, arrayB.length);

        try {
            for (int i = 0; i < arrayA.length; i++) {
                assertEquals(comment + " - array element [" + i + "]:",
                    arrayA[i], arrayB[i]);
            }
        } catch (AssertionFailedError e) {
            for (int i = 0; i < arrayA.length; i++) {
                System.out.print("(" + arrayA[i] + " " + arrayB[i] + ") \n");
            }

            throw e;
        }
    }
}
