
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Tests {@link DefaultLanguageModelFactory}.
 */
public class LanguageModelFactoryTest
{
    @Test
    public void testDefaultLanguageEnglish()
    {
        final ILanguageModel model = new DefaultLanguageModelFactory().getCurrentLanguage();
        assertNotNull(model);
        assertEquals(LanguageCode.ENGLISH, model.getLanguageCode());
    }
    
    @Test
    public void testLanguageDutch()
    {
        final ILanguageModel model = new DefaultLanguageModelFactory().getLanguage(LanguageCode.DUTCH);
        assertNotNull(model);
        assertEquals(LanguageCode.DUTCH, model.getLanguageCode());
    }    
}

