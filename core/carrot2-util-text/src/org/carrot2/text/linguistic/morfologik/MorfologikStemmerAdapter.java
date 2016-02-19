
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

package org.carrot2.text.linguistic.morfologik;

import java.util.List;

import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;

import org.carrot2.text.linguistic.IStemmer;

/**
 * Adapter to Morfologik stemmer.
 */
public class MorfologikStemmerAdapter implements IStemmer
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
