

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
public class StringIntWrapper
    extends AbstractIntWrapper
{
    /** */
    private String stringData;

    /**
     *
     */
    public StringIntWrapper(String stringData)
    {
        super();

        this.stringData = stringData;

        createIntData();
    }

    /**
     * @see com.stachoodev.util.suffixarrays.wrapper.IntWrapper#getStringRepresentation(com.stachoodev.util.suffixarrays.wrapper.Substring)
     */
    public String getStringRepresentation(Substring substring)
    {
        return stringData.substring(substring.getFrom(), substring.getTo());
    }


    /* ------------------------------------------------------------------ protected section */
    /* ------------------------------------------------------------------ private methods */
    /**
     *
     */
    private void createIntData()
    {
        intData = new int[stringData.length() + 1];

        // Any better way to do this ?
        byte [] stringAsBytes = stringData.getBytes();

        for (int i = 0; i < stringData.length(); i++)
        {
            intData[i] = stringAsBytes[i];
        }

        // This is to facilitate suffix comparisons
        intData[stringData.length()] = -1;
    }
}
