
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

package org.carrot2.text.preprocessing;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.*;

public final class TestLanguageModelFactory implements ILanguageModelFactory
{
    private static final TestLanguageModel TEST_LANGUAGE_MODEL = new TestLanguageModel();

    public ILanguageModel getLanguageModel(LanguageCode language)
    {
        return TEST_LANGUAGE_MODEL;
    }

    private final static class TestLanguageModel implements ILanguageModel
    {
        public LanguageCode getLanguageCode()
        {
            return null;
        }

        public IStemmer getStemmer()
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

        public boolean isCommonWord(CharSequence word)
        {
            return word.toString().contains("stop");
        }

        public boolean isStopLabel(CharSequence formattedLabel)
        {
            return formattedLabel.toString().startsWith("stoplabel");
        }

        public ITokenizer getTokenizer()
        {
            return new ExtendedWhitespaceTokenizer();
        }
    }
}
