
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import static org.junit.Assert.assertTrue;

import org.carrot2.core.LanguageCode;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests default tokenizer for {@link LanguageCode#CHINESE_SIMPLIFIED}.
 */
public class DefaultTokenizerFactoryTest
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
        String name = factory.getTokenizer(LanguageCode.CHINESE_SIMPLIFIED).getClass()
            .getName();
        assertTrue(name, name.toLowerCase().indexOf("chinese") >= 0);
    }
}
