package com.dawidweiss.carrot.util.tokenizer.languages;

import java.io.StringReader;

import com.dawidweiss.carrot.core.local.linguistic.LanguageTokenizer;
import com.dawidweiss.carrot.core.local.linguistic.tokens.Token;

import junit.framework.TestCase;

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
