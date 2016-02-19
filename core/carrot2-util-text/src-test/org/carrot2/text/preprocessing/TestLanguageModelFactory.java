
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
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.linguistic.ITokenizerFactory;
import org.carrot2.text.util.MutableCharArray;

public final class TestLanguageModelFactory implements IStemmerFactory,
    ITokenizerFactory, ILexicalDataFactory
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

    @Override
    public ITokenizer getTokenizer(LanguageCode language)
    {
        return new ExtendedWhitespaceTokenizer();
    }

    @Override
    public ILexicalData getLexicalData(LanguageCode language)
    {
        return new ILexicalData()
        {
            public boolean isCommonWord(MutableCharArray word)
            {
                return word.toString().contains("stop");
            }

            public boolean isStopLabel(CharSequence formattedLabel)
            {
                return formattedLabel.toString().startsWith("stoplabel");
            }
        };
    }
}
