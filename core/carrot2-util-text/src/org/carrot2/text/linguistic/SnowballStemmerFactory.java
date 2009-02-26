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

import org.carrot2.util.ReflectionUtils;
import org.tartarus.snowball.SnowballProgram;


/**
 * Factory of {@link IStemmer} implementations from the <code>snowball</code> project.
 */
final class SnowballStemmerFactory
{
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
     * Create and return an {@link IStemmer} adapter for a {@link SnowballStemmer} for a
     * given language code. An identity stemmer is returned for unknown languages.
     */
    public static IStemmer createStemmer(LanguageCode language)
    {
        final String stemmerClazzName = getStemmerClassName(language);
        
        try
        {
            Class<? extends SnowballProgram> stemmerClazz = 
                ReflectionUtils.classForName(stemmerClazzName)
                .asSubclass(SnowballProgram.class);

            return new SnowballStemmerAdapter(stemmerClazz.newInstance());
        }
        catch (Throwable e)
        {
            return new IdentityStemmer();
        }
    }

    /**
     * Construct stemmer class name from a given language.
     */
    private static String getStemmerClassName(LanguageCode language)
    {
        final String name = language.name();
        final String languageName = Character.toUpperCase(
            name.charAt(0)) + name.substring(1).toLowerCase();
        
        return "org.tartarus.snowball.ext." + languageName + "Stemmer";
    }
}
