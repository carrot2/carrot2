
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

import java.util.HashMap;

import org.carrot2.core.LanguageCode;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.*;

/**
 * Factory of {@link IStemmer} implementations from the <code>snowball</code> project.
 */
final class SnowballStemmerFactory
{
    /**
     * Static hard mapping from language codes to stemmer classes in Snowball. This
     * mapping is not dynamic because we want to keep the possibility to obfuscate these
     * classes.
     */
    private static HashMap<LanguageCode, Class<? extends SnowballProgram>> snowballStemmerClasses;
    static
    {
        snowballStemmerClasses = new HashMap<LanguageCode, Class<? extends SnowballProgram>>();
        snowballStemmerClasses.put(LanguageCode.DANISH, DanishStemmer.class);
        snowballStemmerClasses.put(LanguageCode.DUTCH, DutchStemmer.class);
        snowballStemmerClasses.put(LanguageCode.ENGLISH, EnglishStemmer.class);
        snowballStemmerClasses.put(LanguageCode.FINNISH, FinnishStemmer.class);
        snowballStemmerClasses.put(LanguageCode.FRENCH, FrenchStemmer.class);
        snowballStemmerClasses.put(LanguageCode.GERMAN, GermanStemmer.class);
        snowballStemmerClasses.put(LanguageCode.HUNGARIAN, HungarianStemmer.class);
        snowballStemmerClasses.put(LanguageCode.ITALIAN, ItalianStemmer.class);
        snowballStemmerClasses.put(LanguageCode.NORWEGIAN, NorwegianStemmer.class);
        snowballStemmerClasses.put(LanguageCode.PORTUGUESE, PortugueseStemmer.class);
        snowballStemmerClasses.put(LanguageCode.ROMANIAN, RomanianStemmer.class);
        snowballStemmerClasses.put(LanguageCode.RUSSIAN, RussianStemmer.class);
        snowballStemmerClasses.put(LanguageCode.SPANISH, SpanishStemmer.class);
        snowballStemmerClasses.put(LanguageCode.SWEDISH, SwedishStemmer.class);
        snowballStemmerClasses.put(LanguageCode.TURKISH, TurkishStemmer.class);
    }

    /**
     * An adapter converting Snowball programs into {@link IStemmer} interface.
     */
    private static class SnowballStemmerAdapter implements IStemmer
    {
        private final SnowballProgram snowballStemmer;

        public SnowballStemmerAdapter(SnowballProgram snowballStemmer)
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
     * Create and return an {@link IStemmer} adapter for a {@link SnowballProgram} for a
     * given language code. An identity stemmer is returned for unknown languages.
     */
    public static IStemmer createStemmer(LanguageCode language)
    {
        final Class<? extends SnowballProgram> stemmerClazz = snowballStemmerClasses
            .get(language);

        if (stemmerClazz == null)
        {
            org.slf4j.LoggerFactory.getLogger(SnowballStemmerFactory.class).warn(
                "No Snowball stemmer class for: " + language.name());

            throw new RuntimeException("Missing snowball stemmer for: " + language.name());
        }

        try
        {
            return new SnowballStemmerAdapter(stemmerClazz.newInstance());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not instantiate snowball stemmer"
                + " for language: " + language, e);
        }
    }
}
