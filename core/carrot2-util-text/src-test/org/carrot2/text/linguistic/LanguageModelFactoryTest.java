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

