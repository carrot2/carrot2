
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.util.regex.Pattern;

import org.carrot2.core.LanguageCode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test support for {@link LanguageCode#ENGLISH}.
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
    
    @Test
    public void pattern()
    {
        Pattern p = Pattern.compile("(?i)strona główna.*");
        Assert.assertTrue(p.matcher("Strona Główna").matches());
    }
}
