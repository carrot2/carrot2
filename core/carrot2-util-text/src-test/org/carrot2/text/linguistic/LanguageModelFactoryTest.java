
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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests {@link SnowballLanguageModelFactory}.
 */
public class LanguageModelFactoryTest
{
    @Test
    public void testDefaultLanguageEnglish()
    {
        final LanguageModel model = new SnowballLanguageModelFactory().getCurrentLanguage();
        assertNotNull(model);
        assertEquals(LanguageCode.ENGLISH, model.getLanguageCode());
    }
    
    @Test
    public void testLanguageDutch()
    {
        final LanguageModel model = new SnowballLanguageModelFactory().getLanguage(LanguageCode.DUTCH);
        assertNotNull(model);
        assertEquals(LanguageCode.DUTCH, model.getLanguageCode());
    }    
}

