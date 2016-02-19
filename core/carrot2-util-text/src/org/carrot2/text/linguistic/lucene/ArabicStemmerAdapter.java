
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

package org.carrot2.text.linguistic.lucene;

import java.util.Arrays;

import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.util.MutableCharArray;

/**
 * Adapter to lucene-contrib Arabic analyzers.
 */
public class ArabicStemmerAdapter implements IStemmer
{
    private final org.apache.lucene.analysis.ar.ArabicStemmer delegate;
    private final org.apache.lucene.analysis.ar.ArabicNormalizer normalizer;

    private char [] buffer = new char [0];

    public ArabicStemmerAdapter()
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
            return new MutableCharArray(Arrays.copyOf(buffer, newLen));
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
