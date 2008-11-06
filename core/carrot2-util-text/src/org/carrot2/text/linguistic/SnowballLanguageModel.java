package org.carrot2.text.linguistic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.resource.*;
import org.tartarus.snowball.SnowballStemmer;

import com.google.common.collect.Sets;

/**
 * Implements language models on top of Snowball stemmers.
 */
final class SnowballLanguageModel implements LanguageModel
{
    private final LanguageCode languageCode;
    private final Stemmer stemmer;
    private final HashSet<MutableCharArray> stopwords = new HashSet<MutableCharArray>();
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
            /*
             * Snowball programs are single-threaded, so make sure only one thread
             * performs stemming at one time.
             */
            synchronized (snowballStemmer)
            {
                /*
                 * TODO: I think Sebastiano Vigna or one of the mg4j-fellows mentioned a
                 * nice improvement to snowball on the mailing list once, where stemming
                 * was performed on CharSequences directly.
                 */
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
    }

    /**
     * Creates a new language model based on Snowball stemmers and loading resources
     * associated with <code>languageCode</code>'s ISO code.
     */
    @SuppressWarnings("unchecked")
    SnowballLanguageModel(LanguageCode languageCode, ResourceUtils resourceLoaders,
        boolean mergeStopwords)
    {
        this.languageCode = languageCode;

        final Set<String> stopwords = loadCommonWords(resourceLoaders,
            mergeStopwords ? getAllKnownIsoCodes() : new String []
            {
                languageCode.getIsoCode()
            });

        for (String s : stopwords)
        {
            this.stopwords.add(new MutableCharArray(s));
        }

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

    /**
     * Loads common words associated with the given language.
     */
    private static Set<String> loadCommonWords(ResourceUtils resourceLoaders,
        String... isoCodes)
    {
        try
        {
            final Set<String> result = Sets.newHashSet();

            for (String isoCode : isoCodes)
            {
                final Resource commonWordsResource = resourceLoaders.getFirst(
                    "stopwords." + isoCode, SnowballLanguageModel.class);

                if (commonWordsResource == null)
                {
                    // Language resources not found.
                    throw new RuntimeException("Common words not found: " + isoCode);
                }
                result.addAll(TextResourceUtils.load(commonWordsResource));
            }

            return result;
        }
        catch (IOException e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }

    /**
     * Returns an array ISO codes of all {@link LanguageCode}s.
     */
    private static String [] getAllKnownIsoCodes()
    {
        final LanguageCode [] values = LanguageCode.values();
        final String [] result = new String [values.length];
        for (int i = 0; i < values.length; i++)
        {
            result[i] = values[i].getIsoCode();
        }
        return result;
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
