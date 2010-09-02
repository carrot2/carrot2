
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

package org.carrot2.text.linguistic.morfologik;

import java.util.List;

import morfologik.stemming.PolishStemmer;
import morfologik.stemming.WordData;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.IStemmerFactory;


/**
 * Factory of {@link IStemmer} implementations for the {@link LanguageCode#POLISH}
 * language if <a href="http://morfologik.blogspot.com/">Morfologik-stemming</a> library
 * is available in classpath.
 */
public final class PolishStemmerFactory implements IStemmerFactory
{
    /**
     * Adapter to Morfologik stemmer.
     */
    private static class MorfologikStemmerAdapter implements IStemmer
    {
        private final morfologik.stemming.IStemmer stemmer;

        public MorfologikStemmerAdapter()
        {
            this.stemmer = new PolishStemmer();
        }

        public CharSequence stem(CharSequence word)
        {
            final List<WordData> stems = stemmer.lookup(word);
            if (stems == null || stems.size() == 0)
            {
                return null;
            }
            else
            {
                return stems.get(0).getStem().toString();
            }
        }
    }

    @Override
    public IStemmer createInstance()
    {
        return new MorfologikStemmerAdapter();
    }
}
