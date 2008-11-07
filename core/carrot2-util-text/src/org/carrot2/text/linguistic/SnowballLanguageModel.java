
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

package org.carrot2.text.linguistic;

import java.util.Set;

import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;
import org.tartarus.snowball.SnowballStemmer;

/**
 * Implements language models on top of Snowball stemmers.
 */
final class SnowballLanguageModel implements LanguageModel
{
    private final LanguageCode languageCode;
    private final Stemmer stemmer;
    private final Set<MutableCharArray> stopwords;
    private final MutableCharArray buffer = new MutableCharArray("");

    /**
     * An adapter converting Snowball programs into {@link Stemmer} interface.
     */
    private static class SnowballStemmerAdapter implements Stemmer
    {
        private final SnowballStemmer snowballStemmer;

        public SnowballStemmerAdapter(SnowballStemmer snowballStemmer)
        {
            this.snowballStemmer = snowballStemmer;
        }

        public CharSequence stem(CharSequence word)
        {
            snowballStemmer.setCurrent(word.toString());
            if (snowballStemmer.stem())
            {
                return snowballStemmer.getCurrent();
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Creates a new language model based on Snowball stemmers and loading resources
     * associated with <code>languageCode</code>'s ISO code.
     */
    @SuppressWarnings("unchecked")
    SnowballLanguageModel(LanguageCode languageCode, Set<MutableCharArray> stopwords)
    {
        this.languageCode = languageCode;
        this.stopwords = stopwords;

        try
        {
            this.stemmer = createStemmer(languageCode);
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Stemmer class not available.", e);
        }
    }

    /**
     * Create and return a {@link Stemmer} adapter for a {@link SnowballStemmer} for a
     * given language code.
     */
    private static Stemmer createStemmer(LanguageCode language) throws Exception
    {
        final String stemmerClazzName = "org.tartarus.snowball.ext."
            + language.name().toLowerCase() + "Stemmer";

        final Class<? extends SnowballStemmer> stemmerClazz = Thread.currentThread()
            .getContextClassLoader().loadClass(stemmerClazzName).asSubclass(
                SnowballStemmer.class);

        final SnowballStemmer snowballStemmer;

        try
        {
            snowballStemmer = stemmerClazz.newInstance();
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }

        return new SnowballStemmerAdapter(snowballStemmer);
    }

    public LanguageCode getLanguageCode()
    {
        return languageCode;
    }

    public Stemmer getStemmer()
    {
        return stemmer;
    }

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
}
