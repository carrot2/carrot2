package com.dawidweiss.carrot.util.tokenizer.languages;

import java.io.*;

import junit.framework.*;

import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.filter.snowball.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * Tests of the {@link StemmedLanguageBase} base class. 
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class StemmedLanguageBaseTest extends TestCase{
    
    private final static class TestLanguage extends StemmedLanguageBase {
        protected LanguageTokenizer createTokenizerInstanceInternal() {
            return WordBasedParserFactory.Default.borrowParser();
        }

        /**
         * Assume this is English
    	 */
		public String getIsoCode() {
            return "en";
		}

		/** 
         * Return a Porter stemmer instance for this class.
		 * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageBase#createStemmerInstance()
		 */
		protected Stemmer createStemmerInstance() {
            Stemmer s = SnowballStemmersFactory.getInstance(getIsoCode());
            assertNotNull( s );
            return s;
		}
    }
    
    public void testLanguageBaseClass() {
        TestLanguage language = new TestLanguage();
        
        assertNotNull( language.borrowStemmer());
        LanguageTokenizer tokenizer = language.borrowTokenizer();
        try {
            assertNotNull(tokenizer);
            String TEST = "fruiting looping are gross ";
            tokenizer.restartTokenizationOn(new StringReader(TEST));
            Token [] tokens = new Token [ 10 ];

            int num = tokenizer.getNextTokens(tokens, 0);
            assertTrue(num > 0);

            for (int i=0;i<num;i++) {
                assertTrue( tokens[i] != null );
                assertTrue( tokens[i] instanceof StemmedToken );
            }
        } finally {
            language.returnTokenizer(tokenizer);
        }
    }
    
}
