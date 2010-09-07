
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

package org.carrot2.text.linguistic.lucene;

import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.tartarus.snowball.SnowballProgram;

/**
 * A factory of Snowball-based stemmers.
 */
public class SnowballStemmerFactory implements IStemmerFactory
{
    private final Class<? extends SnowballProgram> clazz;
    private final String stemmerClazz;

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

    @SuppressWarnings("unchecked")
    public SnowballStemmerFactory(String snowballClazz)
    {
        this.stemmerClazz = snowballClazz;
        
        Class<?> clz = null;
        try
        {
            clz = Class.forName(snowballClazz, true, 
                Thread.currentThread().getContextClassLoader());
        }
        catch (Throwable t)
        {
            // ignore
        }

        if (clz == null)
        {
            try
            {
                clz = Class.forName(snowballClazz, true, this.getClass().getClassLoader());
            }
            catch (Throwable t)
            {
                // ignore
            }
        }

        clazz = (Class<? extends SnowballProgram>) clz;
    }

    @Override
    public IStemmer createInstance()
    {
        if (clazz == null)
            throw new RuntimeException("Snowball stemmer not available: "
                + stemmerClazz);

        try
        {
            return new SnowballStemmerAdapter(clazz.newInstance());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
