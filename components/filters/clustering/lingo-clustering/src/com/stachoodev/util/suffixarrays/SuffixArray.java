

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



/**
 *
 */
public class SuffixArray
{
    /** */
    protected int [] suffixArray;

    /**
     *
     */
    SuffixArray(int [] suffixArray)
    {
        this.suffixArray = suffixArray;
    }

    /**
     *
     */
    public int [] getSuffixArray()
    {
        return suffixArray;
    }


    /**
     *
     */
    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer("[ ");

        for (int i = 0; i < suffixArray.length; i++)
        {
            stringBuffer.append(Integer.toString(suffixArray[i]));
            stringBuffer.append(" ");
        }

        stringBuffer.append("]");

        return stringBuffer.toString();
    }
}
