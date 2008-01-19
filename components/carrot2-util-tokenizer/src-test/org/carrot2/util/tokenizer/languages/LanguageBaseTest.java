
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

package org.carrot2.util.tokenizer.languages;

import java.io.StringReader;

import junit.framework.TestCase;

import org.carrot2.core.linguistic.LanguageTokenizer;
import org.carrot2.core.linguistic.tokens.Token;

/**
 * Tests of the {@link LanguageBase} base class. 
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LanguageBaseTest extends TestCase{
    
    private final static class TestLanguage extends LanguageBase {
    	/**
         * Returns 'test' for language code.
    	 */
		public String getIsoCode() {
            return "test";
		}
    }
    
    public void testLanguageBaseClass() {
        TestLanguage language = new TestLanguage();
        
        assertNull( language.borrowStemmer());
        LanguageTokenizer tokenizer = language.borrowTokenizer();
        try {
            assertNotNull(tokenizer);
            String TEST = "abc. def.";
            tokenizer.restartTokenizationOn(new StringReader(TEST));
            Token [] tokens = new Token [ 10 ];

            int num = tokenizer.getNextTokens(tokens, 0);
            assertTrue(num > 0);
            
            for (int i=0;i<num;i++) {
                assertTrue( tokens[i] != null );
            }
        } finally {
            language.returnTokenizer(tokenizer);
        }
    }
    
}
