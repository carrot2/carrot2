

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



/**
 *
 */
public abstract class AbstractIntWrapper
    implements IntWrapper
{
    /** */
    protected int [] intData;

    /**
     *
     */
    protected AbstractIntWrapper()
    {
        this(new int [] { -1 });
    }


    /**
     *
     */
    protected AbstractIntWrapper(int [] intData)
    {
        this.intData = intData;
    }

    /**
     *
     */
    public int [] asIntArray()
    {
        return intData;
    }


    /**
     *
     */
    public int length()
    {
        return intData.length - 1;
    }


    /**
     *
     */
    public void reverse()
    {
        int temp;

        for (int i = 0; i < ((intData.length - 1) / 2); i++)
        {
            temp = intData[i];
            intData[i] = intData[intData.length - i - 2];
            intData[intData.length - i - 2] = temp;
        }
    }


    /**
     *
     */
    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer("[ ");

        for (int i = 0; i < intData.length; i++)
        {
            stringBuffer.append(Integer.toString(intData[i]));
            stringBuffer.append(" ");
        }

        stringBuffer.append("]");

        return stringBuffer.toString();
    }
}
