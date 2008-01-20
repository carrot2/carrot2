
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.suffixarrays.wrapper;

/**
 * An IntWrapper for single Strings.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class StringIntWrapper extends IntWrapperBase
{

    /** The original string data */
    private String stringData;

    /**
     * Creates an instance of the StringWrapper based on the provided input
     * String.
     * 
     * @param stringData
     */
    public StringIntWrapper(String stringData)
    {
        super();
        this.stringData = stringData;
        createIntData();
    }

    /**
     * Creates intData.
     */
    private void createIntData()
    {
        intData = new int [stringData.length() + 1];

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