package com.dawidweiss.carrot.util.tokenizer.languages;

import java.io.StringReader;

import com.dawidweiss.carrot.core.local.linguistic.LanguageTokenizer;
import com.dawidweiss.carrot.core.local.linguistic.Stemmer;
import com.dawidweiss.carrot.core.local.linguistic.tokens.StemmedToken;
import com.dawidweiss.carrot.core.local.linguistic.tokens.Token;
import com.dawidweiss.carrot.filter.snowball.SnowballStemmersFactory;
import com.dawidweiss.carrot.util.tokenizer.parser.WordBasedParser;
import com.dawidweiss.carrot.util.common.pools.ReusableObjectsFactory;
import com.dawidweiss.carrot.util.common.pools.SoftReusableObjectsPool;

import junit.framework.TestCase;

/**
 * Tests of the {@link StemmedLanguageBase} base class. 
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class StemmedLanguageBaseTest extends TestCase{
    
    private final static class TestLanguage extends StemmedLanguageBase {
        protected LanguageTokenizer createTokenizerInstanceInternal() {
            return new WordBasedParser(
                    new SoftReusableObjectsPool(
                    		new ReusableObjectsFactory() {
                                public void createNewObjects( Object [] objects ) {
                                    final int max = objects.length;
                                    for (int i=0;i<max;i++) {
                                        objects[i] = new MutableStemmedToken();
                                    }
                            }
                    }, 100,100));
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
                System.out.println( ((StemmedToken) tokens[i]).getStem());
            }
        } finally {
            language.returnTokenizer(tokenizer);
        }
    }
    
}
