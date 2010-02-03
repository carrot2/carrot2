
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

import java.nio.CharBuffer;

import org.carrot2.core.LanguageCode;
import org.slf4j.Logger;


/**
 * Factory of {@link IStemmer} implementations for the {@link LanguageCode#ARABIC}
 * language. Requires <code>lucene-contrib</code> to be present in classpath, otherwise
 * an empty (identity) stemmer is returned.
 */
final class ArabicStemmerFactory
{
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(ArabicStemmerFactory.class); 

    private final static IStemmer stemmer;
    static
    {
        stemmer = createStemmerInternal();
        if (stemmer instanceof IdentityStemmer)
        {
            logger.warn("lucene-contrib classes not available in classpath.");
        }
    }

    /**
     * Adapter to lucene-contrib Arabic analyzers.
     */
    private static class LuceneStemmerAdapter implements IStemmer
    {
        private final org.apache.lucene.analysis.ar.ArabicStemmer delegate;
        private final org.apache.lucene.analysis.ar.ArabicNormalizer normalizer;

        private char [] buffer = new char [0]; 

        public LuceneStemmerAdapter()
            throws Exception
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
                return CharBuffer.wrap(buffer, 0, newLen);
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

    /*
     * 
     */
    public static IStemmer createStemmer()
    {
        return stemmer;
    }

    /**
     * Attempts to instantiate <code>morfologik-stemming</code> (Lametyzator) stemmer.
     */
    private static IStemmer createStemmerInternal()
    {
        try
        {
            return new LuceneStemmerAdapter();
        }
        catch (Throwable e)
        {
            return IdentityStemmer.INSTANCE;
        }
    }
}
