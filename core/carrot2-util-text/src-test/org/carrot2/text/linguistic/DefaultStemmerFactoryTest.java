
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

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link IStemmer}s.
 */
public class DefaultStemmerFactoryTest extends CarrotTestCase
{
    private DefaultStemmerFactory factory;

    @Before
    public void createFactory()
    {
        factory = new DefaultStemmerFactory();
    }

    /**
     * Check if English stemmer from snowball is returned.
     */
    @Test
    public void testEnglishHasSnowballStemmer()
    {
        assertTrue(factory.getStemmer(LanguageCode.ENGLISH).getClass().getName()
            .toLowerCase().indexOf("snowball") >= 0);
    }

    /**
     * Check if Polish stemmer is an adapter to Morfologik.
     */
    @Test
    public void testPolishHasMorfologikStemmer()
    {
        String name = factory.getStemmer(LanguageCode.POLISH).getClass().getName();
        assertTrue(name, name.toLowerCase().indexOf("morfologik") >= 0);
    }
}
