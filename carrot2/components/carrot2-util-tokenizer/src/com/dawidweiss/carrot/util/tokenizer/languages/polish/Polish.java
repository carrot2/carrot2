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
package com.dawidweiss.carrot.util.tokenizer.languages.polish;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * An implementation of {@link Language} interface
 * for the Polish language.
 * 
 * <p>Requires <code>carrot2-stemmer-lametyzator</code> for
 * stemming capabilities.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class Polish extends StemmedLanguageBase {

    /**
     * A set of stopwords for this language.
     */
    private final static Set stopwords;
    
    /*
     * Load stopwords from an associated resource.
     */
    static {
        String resourceName = "/com/dawidweiss/carrot/util/tokenizer/languages/polish/stopwords.pl";
        try {
			stopwords = LanguageBase.loadStopwords(
			    Polish.class.getResourceAsStream(resourceName));
		} catch (IOException e) {
            throw new RuntimeException("Could not load the required" +
                    "resource: " + resourceName);
		}
    }

    
    /**
     * Public constructor. 
     */
    public Polish() {
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
     * @return Language code: <code>pl</code>
	 * @see com.dawidweiss.carrot.core.local.linguistic.Language#getIsoCode()
	 */
	public String getIsoCode() {
        return "pl";
	}

    /** 
     * Return an instance of an Polish stemmer (Stempel stemmer is used).
     *  
     * @see com.dawidweiss.carrot.util.tokenizer.languages.LanguageBase#createStemmerInstance()
     */
    protected Stemmer createStemmerInstance() {
		return new com.dawidweiss.carrot.filter.stempel.Stempel();
    }

}
