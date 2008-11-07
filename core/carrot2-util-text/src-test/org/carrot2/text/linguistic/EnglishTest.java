
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

package org.carrot2.text.linguistic;

/**
 * 
 */
public class EnglishTest extends LanguageModelTestBase
{
    protected LanguageCode getLanguageCode()
    {
        return LanguageCode.ENGLISH;
    }

    protected String [][] getStemmingTestData()
    {
        return new String [] []
        {
            {
                "pulps", "pulp"
            },
            {
                "driving", "drive"
            },
            {
                "king's", "king"
            },
            {
                "mining", "mine"
            }
        };
    }

    protected String [] getCommonWordsTestData()
    {
        return new String []
        {
            "and", "or", "to", "from"
        };
    }
}
