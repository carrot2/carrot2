

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.util.suffixarrays;


import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import com.stachoodev.util.suffixarrays.wrapper.IntWrapper;
import com.stachoodev.util.suffixarrays.wrapper.StringIntWrapper;


/**
 *
 */
public class LcpSuffixSorterTest
    extends TestCase
{
    /** */
    private LcpSuffixSorter [] algorithms;

    /** */
    private static final IntWrapper emptySuffixData = new StringIntWrapper("");
    private static final int [] emptySuffixDataResult = new int [] {  };
    private static final int [] emptySuffixDataLcp = new int [] { 0 };

    /** */
    private static final IntWrapper suffixData = new StringIntWrapper("to_be_or_not_to_be");
    private static final int [] suffixDataResult = new int []
        {
            15, 2, 8, 5, 12, 16, 3, 17, 4, 9, 14, 1, 6, 10, 7, 11, 13, 0
        };
    private static final int [] suffixDataLcp = new int []
        {
            0, 3, 1, 1, 1, 0, 2, 0, 1, 0, 0, 4, 1, 1, 0, 0, 1, 5, 0
        };

    /**
     *
     */
    public void setUp()
    {
        algorithms = new LcpSuffixSorter [] { new DummyLcpSuffixSorter() };
    }


    /**
     *
     */
    public void testSorting()
    {
        for (int i = 0; i < algorithms.length; i++)
        {
            assertEquals(
                algorithms[i].getClass().toString() + " - EmptyData", emptySuffixDataResult,
                algorithms[i].suffixSort(emptySuffixData).getSuffixArray()
            );

            assertEquals(
                algorithms[i].getClass().toString() + " - NonemptyData", suffixDataResult,
                algorithms[i].suffixSort(suffixData).getSuffixArray()
            );
        }
    }


    /**
     *
     */
    public void testLcp()
    {
        for (int i = 0; i < algorithms.length; i++)
        {
            assertEquals(
                algorithms[i].getClass().toString() + " - EmptyData", emptySuffixDataLcp,
                algorithms[i].lcpSuffixSort(emptySuffixData).getLcpArray()
            );

            assertEquals(
                algorithms[i].getClass().toString() + " - NonemptyData", suffixDataLcp,
                algorithms[i].lcpSuffixSort(suffixData).getLcpArray()
            );
        }
    }


    /**
     *
     */
    protected void assertEquals(String comment, int [] arrayA, int [] arrayB)
    {
        assertEquals(comment + " - arrayLengths:", arrayA.length, arrayB.length);

        try
        {
            for (int i = 0; i < arrayA.length; i++)
            {
                assertEquals(comment + " - array element [" + i + "]:", arrayA[i], arrayB[i]);
            }
        }
        catch (AssertionFailedError e)
        {
            for (int i = 0; i < arrayA.length; i++)
            {
                System.out.print("(" + arrayA[i] + " " + arrayB[i] + ") \n");
            }

            throw e;
        }
    }
}
