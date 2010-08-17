
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;
import org.carrot2.core.Platform;
import org.junit.Assume;
import org.junit.Before;

/**
 * Test support for {@link LanguageCode#POLISH}.
 */
public class PolishTest extends LanguageModelTestBase
{
    @Before
    public void before()
    {
        Assume.assumeTrue(Platform.getPlatform() == Platform.JAVA);
    }

    protected LanguageCode getLanguageCode()
    {
        return LanguageCode.POLISH;
    }

    protected String [][] getStemmingTestData()
    {
        return new String [] []
        {
            {
                "okropnymi", "okropny"
            },
            {
                "owocami", "owoc"
            }
        };
    }

    protected String [] getCommonWordsTestData()
    {
        return new String []
        {
            "aby", "albo", "bez", "i"
        };
    }
}
