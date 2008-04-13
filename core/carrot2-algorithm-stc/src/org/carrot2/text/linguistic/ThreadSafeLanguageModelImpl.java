package org.carrot2.text.linguistic;

import java.util.*;

import org.carrot2.text.MutableCharArray;

/**
 * Implements {@link LanguageModel}.
 */
abstract class ThreadSafeLanguageModelImpl implements LanguageModel
{
    private final LanguageCode language;

    private final Set<MutableCharArray> stopwords;
    private final MutableCharArray buffer = new MutableCharArray("");

    private final ThreadLocal<Stemmer> perThreadStemmer = new ThreadLocal<Stemmer>();

    /**
     * 
     */
    ThreadSafeLanguageModelImpl(LanguageCode language, Set<String> stopwords)
    {
        this.language = language;
        this.stopwords = new HashSet<MutableCharArray>();
        for (String s : stopwords)
        {
            this.stopwords.add(new MutableCharArray(s));
        }
    }

    /**
     * Checks for common words in {@link #stopwords}.
     */
    public boolean isCommonWord(CharSequence sequence)
    {
        if (sequence instanceof MutableCharArray)
        {
            return stopwords.contains((MutableCharArray) sequence);
        }
        else
        {
            buffer.reset(sequence);
            return stopwords.contains(buffer);
        }
    }

    /**
     * Returns a per-thread allocated stemmer instance.
     */
    public final Stemmer getStemmer()
    {
        Stemmer stemmerInstance = perThreadStemmer.get();
        if (stemmerInstance == null)
        {
            stemmerInstance = createStemmer();
            perThreadStemmer.set(stemmerInstance);
        }

        return stemmerInstance;
    }

    /**
     * @return Language code for this language.
     */
    public final LanguageCode getLanguageCode()
    {
        return language;
    }

    /**
     * @return Subclasses must provide new {@link Stemmer} instances here.
     */
    protected abstract Stemmer createStemmer();
}
