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

import org.apache.log4j.Logger;
import org.carrot2.util.ReflectionUtils;


/**
 * Factory of {@link IStemmer} implementations from the {@link LanguageCode#POLISH}
 * language. If <a href="http://morfologik.blogspot.com/">Morfologik-stemming</a> library
 * is available in classpath, a wrapper around this library is returned. Otherwise an
 * empty identity stemmer is returned.
 */
final class PolishStemmerFactory
{
    private final static Logger logger = Logger.getLogger(PolishStemmerFactory.class); 

    private final static IStemmer stemmer;
    static
    {
        stemmer = createStemmerInternal();
        if (stemmer instanceof IdentityStemmer)
        {
            logger.warn("Morfologik classes not available in classpath.");
        }
    }

    /**
     * An adapter converting Snowball programs into {@link IStemmer} interface.
     */
    private static class MorfologikStemmerAdapter implements IStemmer
    {
        private final morfologik.stemmers.IStemmer stemmer;

        public MorfologikStemmerAdapter()
            throws Exception
        {
            final String stemmerClazzName = "morfologik.stemmers.Lametyzator";

            final Class<? extends morfologik.stemmers.IStemmer> stemmerClazz = 
                ReflectionUtils.classForName(stemmerClazzName)
                .asSubclass(morfologik.stemmers.IStemmer.class);

            this.stemmer = stemmerClazz.newInstance();
        }

        public CharSequence stem(CharSequence word)
        {
            final String [] stems = stemmer.stem(word.toString());
            if (stems == null || stems.length == 0)
            {
                return null;
            }
            else
            {
                return stems[0];
            }
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
            return new MorfologikStemmerAdapter();
        }
        catch (Throwable e)
        {
            return new IdentityStemmer();
        }
    }
}
