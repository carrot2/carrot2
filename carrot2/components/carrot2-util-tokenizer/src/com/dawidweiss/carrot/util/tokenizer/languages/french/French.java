/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.languages.french;

import java.io.IOException;
import java.util.Set;

import com.dawidweiss.carrot.core.local.linguistic.LanguageTokenizer;
import com.dawidweiss.carrot.core.local.linguistic.Stemmer;
import com.dawidweiss.carrot.filter.snowball.SnowballStemmersFactory;
import com.dawidweiss.carrot.util.common.pools.ReusableObjectsFactory;
import com.dawidweiss.carrot.util.common.pools.SoftReusableObjectsPool;
import com.dawidweiss.carrot.util.tokenizer.languages.LanguageBase;
import com.dawidweiss.carrot.util.tokenizer.languages.MutableStemmedToken;
import com.dawidweiss.carrot.util.tokenizer.languages.StemmedLanguageBase;
import com.dawidweiss.carrot.util.tokenizer.parser.WordBasedParser;

/**
 * An implementation of {@link Language} interface
 * for French.
 * 
 * <p>Requires <code>carrot2-stemmer-snowball</code> for
 * stemming capabilities
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class French extends StemmedLanguageBase {

    /**
     * A set of stopwords for this language.
     */
    private final static Set stopwords;
    
    /*
     * Load stopwords from an associated resource.
     */
    static {
        String resourceName = "stopwords.fr";
        try {
			stopwords = LanguageBase.loadStopwords(
			    French.class.getResourceAsStream(resourceName));
		} catch (IOException e) {
            throw new RuntimeException("Could not load the required" +
                    "resource: " + resourceName);
		}
    }

    
    /**
     * Public constructor. 
     */
    public French() {
        super.setStopwords(stopwords);
    }
    
	/**
     * Creates a new instance of a {@link LanguageTokenizer} for 
     * this language.
     * 
     * <p>The tokenizer is a {@link WordBasedTokenizer} with an internal
     * soft pool for tokens.
     * 
	 * @see com.dawidweiss.carrot.util.tokenizer.languages.StemmedLanguageBase#createTokenizerInstanceInternal()
	 */
	protected LanguageTokenizer createTokenizerInstanceInternal() {
        // these constants may seem like off-the top of one's head
        // but they are a result of some testing and experimenting
        // with the soft pool.
        final int HARD_TOKENS_POOL_SIZE = 500;
        final int SOFT_TOKENS_POOL_INCREMENT = 500;
        
        return new WordBasedParser(
                new SoftReusableObjectsPool(
                        new ReusableObjectsFactory() {
                            public void createNewObjects( Object [] objects ) {
                                final int max = objects.length;
                                for (int i=0;i<max;i++) {
                                    objects[i] = new MutableStemmedToken();
                                }
                            }
                        }, HARD_TOKENS_POOL_SIZE, SOFT_TOKENS_POOL_INCREMENT));
	}

	/**
     * @return Language code: <code>fr</code>
	 * @see com.dawidweiss.carrot.core.local.linguistic.Language#getIsoCode()
	 */
	public String getIsoCode() {
        return "fr";
	}

    /** 
     * Return an instance of a stemmer for this language
     * from the Snowball component.
     *  
     * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageBase#createStemmerInstance()
     */
    protected Stemmer createStemmerInstance() {
        return SnowballStemmersFactory.getInstance(getIsoCode());
    }


}
