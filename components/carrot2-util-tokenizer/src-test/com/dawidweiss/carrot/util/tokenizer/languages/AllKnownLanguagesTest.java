
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.tokenizer.languages;

import junit.framework.TestCase;

/**
 * Tests instantiation of all known languages.
 * 
 * @author Dawid Weiss
 */
public class AllKnownLanguagesTest extends TestCase {

    public AllKnownLanguagesTest(String s) {
        super(s);
    }

    public void testAvailableLanguages() {
        assertEquals(AllKnownLanguages.languageClasses.length,
                AllKnownLanguages.getLanguageCodes().length);
    }
}
