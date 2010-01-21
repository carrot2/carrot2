
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

import static org.junit.Assert.*;

import org.carrot2.core.LanguageCode;
import org.junit.Test;

/**
 * Tests {@link DefaultLanguageModelFactory}.
 */
public class LanguageModelFactoryTest
{
    @Test
    public void testLanguageDutch()
    {
        final ILanguageModel model = new DefaultLanguageModelFactory().getLanguageModel(LanguageCode.DUTCH);
        assertNotNull(model);
        assertEquals(LanguageCode.DUTCH, model.getLanguageCode());
    }
    
    @Test
    public void testLinguisticResourcesAvailable()
    {
        for (LanguageCode l : LanguageCode.values())
        {
            new DefaultLanguageModelFactory().getLanguageModel(l);
        }
        
        assertFalse("There were problems with loading certain lexical resources. Check the logs.", 
            LexicalResources.hasIssues());
    }
}

