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
