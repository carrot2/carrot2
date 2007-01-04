
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.tokenizer.languages;

import java.io.StringReader;
import java.util.*;

import junit.framework.TestCase;

import org.carrot2.core.linguistic.*;
import org.carrot2.core.linguistic.tokens.*;

/**
 * Base class for implementations of
 * {@link Language} interface. 
 *  
 * @author Dawid Weiss
 * @version $Revision$
 */
public abstract class LanguageImplTestBase extends TestCase {

	public LanguageImplTestBase() {
		super();
	}

	public LanguageImplTestBase(String arg0) {
		super(arg0);
	}
    
    protected abstract Language getLanguageInstance();
    protected String [][] getWordsToCompare() {
        return new String[0][];
    }

    public void testTokenizerAvailability() {
        Language en = getLanguageInstance();
        LanguageTokenizer tokenizer = en.borrowTokenizer();
        try {
            assertNotNull(tokenizer);
        } finally {
            if (tokenizer != null)
            	en.returnTokenizer(tokenizer);
        }
    }
    
	public void testStemmerAvailability() {
        Language en = getLanguageInstance();
        Stemmer stemmer = en.borrowStemmer();
        try {
            assertNotNull(stemmer);
        } finally {
            if (stemmer != null)
                en.returnStemmer(stemmer);
        }
    }

    public void testStopWordsExist() {
        Language en = getLanguageInstance();
        assertNotNull( en.getStopwords() );
        assertTrue( en.getStopwords().size() > 0);
    }

    public void testLangCodeNotNull() {
        Language en = getLanguageInstance();
        assertNotNull( en.getIsoCode() );
    }
    
    public void testCompareWordsToStems() {
        Language lang = getLanguageInstance();
        Stemmer stemmer = lang.borrowStemmer();
        String [][] forms = this.getWordsToCompare();

        try {
            assertNotNull(stemmer);
            for (int i=0;i<forms.length;i++) {
                String word = forms[i][0];
                String base = stemmer.getStem(word.toCharArray(),0, word.length());
                if (!forms[i][1].equals(base)) {
                    System.out.println(base + ", should be: " + forms[i][1]);
                }
                assertEquals(forms[i][1], base);
            }
        } finally {
            if (stemmer != null)
                lang.returnStemmer(stemmer);
        }
    }

    public void testStopWordsDetection() {
        Language lang = getLanguageInstance();
        LanguageTokenizer tokenizer = lang.borrowTokenizer();
        Set stopwords = lang.getStopwords();

        try {
            assertNotNull(tokenizer);
            for (Iterator i = stopwords.iterator(); i.hasNext();) {
                String word = (String) i.next();
                tokenizer.restartTokenizationOn(new StringReader(word));
                
                Token [] tokens = new Token[ 2 ];
                int recognized = tokenizer.getNextTokens(tokens, 0);
                assertEquals( "Word: " + word
                       + " stem: " + ((StemmedToken) tokens[0]).getStem(), 1, recognized );
                
                assertTrue(tokens[0] instanceof TypedToken);
                assertTrue("Word: " + word +
                        " stem: " + ((StemmedToken) tokens[0]).getStem(), (((TypedToken) tokens[0]).getType() &
                        TypedToken.TOKEN_FLAG_STOPWORD) != 0 );
            }
        } finally {
            if (tokenizer != null)
                lang.returnTokenizer(tokenizer);
        }
    }

    public void testNonStopWordsDetection() {
        Language lang = getLanguageInstance();
        LanguageTokenizer tokenizer = lang.borrowTokenizer();
        Token [] tokens = new Token[10];
        
        Set stopwords = new HashSet(
                Arrays.asList(
                        new String [] {
                                "abchde",
                                "aiugt",
                                "sloigh"
                        }));

        try {
            assertNotNull(tokenizer);
            for (Iterator i = stopwords.iterator(); i.hasNext();) {
                String word = (String) i.next();
                tokenizer.restartTokenizationOn(new StringReader(word));
                
                Token [] tokensd = new Token[ 2 ];
                int recognized = tokenizer.getNextTokens(tokens, 0);
                assertEquals( 1, recognized );
                
                assertTrue(tokens[0] instanceof TypedToken);
                assertTrue( (((TypedToken) tokens[0]).getType() &
                        TypedToken.TOKEN_FLAG_STOPWORD) == 0 );
            }
        } finally {
            if (tokenizer != null)
                lang.returnTokenizer(tokenizer);
        }
    }
}
