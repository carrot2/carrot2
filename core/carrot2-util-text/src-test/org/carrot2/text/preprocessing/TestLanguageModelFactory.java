
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

package org.carrot2.text.preprocessing;

import org.carrot2.text.linguistic.*;

public final class TestLanguageModelFactory implements LanguageModelFactory
{
    private static final TestLanguageModel TEST_LANGUAGE_MODEL = new TestLanguageModel();

    public LanguageModel getCurrentLanguage()
    {
        return TEST_LANGUAGE_MODEL;
    }

    public LanguageModel getLanguage(LanguageCode language)
    {
        return getCurrentLanguage();
    }

    private final static class TestLanguageModel implements LanguageModel
    {
        public LanguageCode getLanguageCode()
        {
            return null;
        }

        public Stemmer getStemmer()
        {
            return new Stemmer()
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

        public boolean isCommonWord(CharSequence word)
        {
            return word.toString().contains("stop");
        }
    }
}