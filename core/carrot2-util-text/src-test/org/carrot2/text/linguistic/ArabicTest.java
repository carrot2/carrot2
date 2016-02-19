
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

import org.carrot2.core.LanguageCode;


/**
 * Test support for {@link LanguageCode#ENGLISH}.
 */
public class ArabicTest extends LanguageModelTestBase
{
    protected LanguageCode getLanguageCode()
    {
        return LanguageCode.ARABIC;
    }

    protected String [][] getStemmingTestData()
    {
        return new String [] []
        {
            { "الحسن", "حسن"}, 
            { "والحسن", "حسن"}, 
            { "بالحسن", "حسن"}, 
            { "كالحسن", "حسن"}, 
            { "فالحسن", "حسن"}, 
            { "للاخر", "اخر"},  
            { "وحسن", "حسن"}, 
            { "زوجها", "زوج"}, 
            { "ساهدان", "ساهد"}, 
            { "ساهدات", "ساهد"}, 
            { "ساهدون", "ساهد"}, 
            { "ساهدين", "ساهد"}, 
            { "ساهديه", "ساهد"}, 
            { "ساهدية", "ساهد"}, 
            { "ساهده", "ساهد"}, 
            { "ساهدة", "ساهد"}, 
            { "ساهدي", "ساهد"}, 
            { "وساهدون", "ساهد"}, 
            { "ساهدهات", "ساهد"},  
        };
    }

    protected String [] getCommonWordsTestData()
    {
        return new String []
        {
            "باستثناء",
            "اكثر",
        };
    }
}
