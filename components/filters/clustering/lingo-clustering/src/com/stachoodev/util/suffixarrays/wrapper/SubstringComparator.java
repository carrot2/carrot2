

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


package com.stachoodev.util.suffixarrays.wrapper;


import java.util.Comparator;


/**
 * Compares instances of the Substring class referring to given IntWrapper instance.
 */
public class SubstringComparator
    implements Comparator
{
    /** */
    private int [] intData;

    /**
     *
     */
    public SubstringComparator(IntWrapper intWrapper)
    {
        this.intData = intWrapper.asIntArray();
    }

    /**
     * Note: Shorter strings are bigger !
     */
    public int compare(Object s1, Object s2)
    {
        if (!((s1 instanceof Substring) && (s2 instanceof Substring)))
        {
            throw new ClassCastException(s1.getClass().toString());
        }

        int s1From = ((Substring) s1).getFrom();
        int s1To = ((Substring) s1).getTo();
        int s2From = ((Substring) s2).getFrom();
        int s2To = ((Substring) s2).getTo();

        if (((s2To - s2From) == (s1To - s1From)) && ((s2To - s2From) == 0))
        {
            return 0;
        }

        for (int i = 0; i < (((s2To - s2From) < (s1To - s1From)) ? (s2To - s2From)
                                                                     : (s1To - s1From)); i++)
        {
            if (intData[s1From + i] < intData[s2From + i])
            {
                return -1;
            }
            else if (intData[s1From + i] > intData[s2From + i])
            {
                return 1;
            }
        }

        if ((s2To - s2From) < (s1To - s1From))
        {
            return -1;
        }
        else if ((s2To - s2From) > (s1To - s1From))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
