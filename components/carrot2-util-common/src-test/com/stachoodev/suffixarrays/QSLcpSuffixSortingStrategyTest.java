
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
package com.stachoodev.suffixarrays;

import com.stachoodev.suffixarrays.wrapper.*;

import junit.framework.TestCase;
import junitx.framework.*;

/**
 *
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class QSLcpSuffixSortingStrategyTest extends TestCase
{
    /** Algorithms to be tested */
    private LcpSuffixSortingStrategy [] algorithms;

    /** Empty data */
    private static final IntWrapper emptySuffixData = new StringIntWrapper("");

    /** Empty data suffix array */
    private static final int [] emptySuffixDataResult = new int [] {};

    /** Empty data LCP array */
    private static final int [] emptySuffixDataLcp = new int []
    { 0 };

    /** Non-empty data */
    private static final IntWrapper suffixData = new StringIntWrapper(
        "to_be_or_not_to_be");

    /** Non-empty data suffix array */
    private static final int [] suffixDataResult = new int []
    { 15, 2, 8, 5, 12, 16, 3, 17, 4, 9, 14, 1, 6, 10, 7, 11, 13, 0 };

    /** Non-empty data LCP array */
    private static final int [] suffixDataLcp = new int []
    { 0, 3, 1, 1, 1, 0, 2, 0, 1, 0, 0, 4, 1, 1, 0, 0, 1, 5, 0 };

    /**
     *
     */
    public void setUp()
    {
        algorithms = new LcpSuffixSortingStrategy []
        { new QSLcpSuffixSortingStrategy() };
    }

    /**
     *
     */
    public void testSorting()
    {
        for (int i = 0; i < algorithms.length; i++)
        {
            ArrayAssert.assertEquals(algorithms[i].getClass().toString()
                + " - EmptyData", emptySuffixDataResult, algorithms[i]
                .lcpSuffixSort(emptySuffixData).getSuffixArray());

            ArrayAssert.assertEquals(algorithms[i].getClass().toString()
                + " - NonemptyData", suffixDataResult, algorithms[i]
                .lcpSuffixSort(suffixData).getSuffixArray());
        }
    }

    /**
     *
     */
    public void testLcp()
    {
        for (int i = 0; i < algorithms.length; i++)
        {
            ArrayAssert.assertEquals(algorithms[i].getClass().toString()
                + " - EmptyData", emptySuffixDataLcp, algorithms[i]
                .lcpSuffixSort(emptySuffixData).getLcpArray());

            ArrayAssert.assertEquals(algorithms[i].getClass().toString()
                + " - NonemptyData", suffixDataLcp, algorithms[i]
                .lcpSuffixSort(suffixData).getLcpArray());
        }
    }
}