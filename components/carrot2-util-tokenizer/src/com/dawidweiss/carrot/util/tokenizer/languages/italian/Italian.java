/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.languages.italian;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.filter.snowball.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * An implementation of {@link Language} interface
 * for Italian.
 * 
 * <p>Requires <code>carrot2-stemmer-snowball</code> for
 * stemming capabilities
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class Italian extends StemmedLanguageBase {

    /**
     * A set of stopwords for this language.
     */
    private final static Set stopwords;
    
    /*
     * Load stopwords from an associated resource.
     */
    static {
        String resourceName = "stopwords.it";
        try {
			stopwords = LanguageBase.loadStopwords(
			    Italian.class.getResourceAsStream(resourceName));
		} catch (IOException e) {
            throw new RuntimeException("Could not load the required" +
                    "resource: " + resourceName);
		}
    }

    
    /**
     * Public constructor. 
     */
    public Italian() {
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
        // TODO: This tokenizer is never returned to the pool, but actually
        // all languages could share the same tokenizer pool
        return WordBasedParserFactory.Default.borrowParser();
	}

	/**
     * @return Language code: <code>it</code>
	 * @see com.dawidweiss.carrot.core.local.linguistic.Language#getIsoCode()
	 */
	public String getIsoCode() {
        return "it";
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
