
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

package org.carrot2.text.preprocessing;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.IStemmerFactory;

public final class TestStemmerFactory implements IStemmerFactory
{
    @Override
    public IStemmer getStemmer(LanguageCode language)
    {
        return new IStemmer()
        {
            public CharSequence stem(CharSequence word)
            {
                if (word.length() > 2)
                {
                    return word.subSequence(0, word.length() - 2);
                }
                else
                {
                    return null;
                }
            }
        };
    }
}
