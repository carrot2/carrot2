package org.carrot2.text.linguistic.morfologik;

import java.util.List;

import morfologik.stemming.PolishStemmer;
import morfologik.stemming.WordData;

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