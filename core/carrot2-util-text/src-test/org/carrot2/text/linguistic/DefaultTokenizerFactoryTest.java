
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
 * Tests {@link DefaultTokenizerFactory}.
 */
public class DefaultTokenizerFactoryTest extends CarrotTestCase
{
    private DefaultTokenizerFactory factory;

    @Before
    public void createFactory()
    {
        factory = new DefaultTokenizerFactory();
    }

    /**
     * Check if smart Chinese tokenizer is returned.
     */
    @Test
    public void testChineseHasSmartChineseTokenizer()
    {
        String name = 
            factory.getTokenizer(LanguageCode.CHINESE_SIMPLIFIED).getClass().getName();
        assertTrue(name, name.toLowerCase().indexOf("chinese") >= 0);
    }
}
