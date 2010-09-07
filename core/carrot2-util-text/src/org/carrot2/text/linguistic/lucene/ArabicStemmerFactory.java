
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

package org.carrot2.text.linguistic.lucene;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.util.MutableCharArray;

/**
 * Stemmer factory for Arabic.
 * 
 * @see LanguageCode#ARABIC
 */
public class ArabicStemmerFactory implements IStemmerFactory
{
    /**
     * Adapter to lucene-contrib Arabic analyzers.
     */
    private static class LuceneStemmerAdapter implements IStemmer
    {
        private final org.apache.lucene.analysis.ar.ArabicStemmer delegate;
        private final org.apache.lucene.analysis.ar.ArabicNormalizer normalizer;

        private char [] buffer = new char [0];

        private LuceneStemmerAdapter()
        {
            delegate = new org.apache.lucene.analysis.ar.ArabicStemmer();
            normalizer = new org.apache.lucene.analysis.ar.ArabicNormalizer();
        }

        public CharSequence stem(CharSequence word)
        {
            if (word.length() > buffer.length)
            {
                buffer = new char [word.length()];
            }

            for (int i = 0; i < word.length(); i++)
            {
                buffer[i] = word.charAt(i);
            }

            int newLen = normalizer.normalize(buffer, word.length());
            newLen = delegate.stem(buffer, newLen);

            if (newLen != word.length() || !equals(buffer, newLen, word))
            {
                return new MutableCharArray(buffer, 0, newLen);
            }

            // Same-same.
            return null;
        }

        private boolean equals(char [] buffer, int len, CharSequence word)
        {
            assert len == word.length();

            for (int i = 0; i < len; i++)
            {
                if (buffer[i] != word.charAt(i)) return false;
            }

            return true;
        }
    }

    @Override
    public IStemmer createInstance()
    {
        return new LuceneStemmerAdapter();
    }
}
