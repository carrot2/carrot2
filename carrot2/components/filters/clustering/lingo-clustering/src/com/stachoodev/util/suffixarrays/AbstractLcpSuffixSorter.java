

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


import com.stachoodev.util.suffixarrays.wrapper.IntWrapper;


/**
 *
 */
public abstract class AbstractLcpSuffixSorter
    implements LcpSuffixSorter
{
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
    static int compareSuffixes(int suffixA, int suffixB, int [] suffixData)
    {
        // Reflexiveness
        if (suffixA == suffixB)
        {
            return 0;
        }

        while (
            (suffixA < suffixData.length) && (suffixB < suffixData.length)
                && (suffixData[suffixA] == suffixData[suffixB])
        )
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

    /* ------------------------------------------------------------------ private methods */
}
