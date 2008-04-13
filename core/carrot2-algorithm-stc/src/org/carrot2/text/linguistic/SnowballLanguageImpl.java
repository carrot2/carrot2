package org.carrot2.text.linguistic;

import java.io.IOException;
import java.util.Set;

import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.resource.*;
import org.tartarus.snowball.SnowballStemmer;

/**
 * Implements language models on top of Snowball stemmers.
 */
final class SnowballLanguageImpl extends ThreadSafeLanguageModelImpl
{
    private Class<SnowballStemmer> stemmerClazz;

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
             * TODO: I think Sebastiano Vigna or one of the mg4j-fellows mentioned a nice
             * improvement to snowball on the mailing list once, where stemming was
             * performed on CharSequences directly.
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
    };

    /**
     * Creates a new language model based on Snowball stemmers and loading resources
     * associated with <code>languageCode</code>'s ISO code.
     */
    @SuppressWarnings("unchecked")
    SnowballLanguageImpl(LanguageCode languageCode, ResourceUtils resourceLoaders)
    {
        super(languageCode, loadCommonWords(languageCode.getIsoCode(), resourceLoaders));
        
        try
        {
            final String stemmerClazzName = "org.tartarus.snowball.ext."
                + languageCode.name().toLowerCase() + "Stemmer";
            final Class<?> stemmerClazz = Thread.currentThread().getContextClassLoader()
                .loadClass(stemmerClazzName);

            this.stemmerClazz = (Class<SnowballStemmer>) stemmerClazz;
            createStemmer();
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Stemmer class not available.", e);
        }
    }

    /*
     * 
     */
    @Override
    protected Stemmer createStemmer()
    {
        final SnowballStemmer snowballStemmer;

        try
        {
            snowballStemmer = stemmerClazz.newInstance();
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAs(RuntimeException.class, e);
        }

        return new SnowballStemmerAdapter(snowballStemmer);
    }

    /**
     * Loads common words associated with the given language.
     */
    private static Set<String> loadCommonWords(String isoCode,
        ResourceUtils resourceLoaders)
    {
        try
        {
            final Resource commonWordsResource = resourceLoaders.getFirst("stopwords."
                + isoCode, SnowballLanguageImpl.class);

            if (commonWordsResource == null)
            {
                // Language resources not found.
                throw new RuntimeException("Common words not found: " + isoCode);
            }

            return TextResourceUtils.load(commonWordsResource);
        }
        catch (IOException e)
        {
            throw ExceptionUtils.wrapAs(RuntimeException.class, e);
        }
    }
}
