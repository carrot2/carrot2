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

package org.carrot2.text.linguistic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.carrot2.core.LanguageCode;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link DefaultLanguageModelFactory}.
 */
public class LanguageModelFactoryTest
{
    private DefaultLanguageModelFactory factory;

    @Before
    public void createFactory()
    {
        factory = new DefaultLanguageModelFactory();
    }

    @Test
    public void testLanguageDutch()
    {
        final ILanguageModel model = factory.getLanguageModel(LanguageCode.DUTCH);
        assertNotNull(model);
        assertEquals(LanguageCode.DUTCH, model.getLanguageCode());
    }

    @Test
    public void testLinguisticResourcesAvailable()
    {
        for (LanguageCode l : LanguageCode.values())
        {
            factory.getLanguageModel(l);
        }

        assertFalse(
            "There were problems with loading certain lexical resources. Check the logs.",
            LexicalResources.hasIssues());
    }

    @Test
    public void testResourcesPath()
    {
        try
        {
            factory.resourcePath = "/nonexisting/";
            factory.reloadResources = true;
            factory.getLanguageModel(LanguageCode.ENGLISH);

            // If we're unable to load resources, the resource path setting must be
            // working.
            // If the previous test passes too, this gives us good chances that the
            // resourcePath setting actually works.
            assertTrue(LexicalResources.hasIssues());
        }
        finally
        {
            // Reload correct resource to the static data structures
            factory.resourcePath = "/";
            LexicalResources.clearIssues();
            testLinguisticResourcesAvailable();
        }
    }
}
