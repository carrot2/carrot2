
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

package com.dawidweiss.carrot.util.tokenizer.languages.norwegian;

import java.io.IOException;
import java.util.Set;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.core.local.linguistic.LanguageTokenizer;
import com.dawidweiss.carrot.core.local.linguistic.Stemmer;
import com.dawidweiss.carrot.filter.snowball.SnowballStemmersFactory;
import com.dawidweiss.carrot.util.tokenizer.languages.StemmedLanguageBase;
import com.dawidweiss.carrot.util.tokenizer.parser.WordBasedParserFactory;
import com.stachoodev.util.common.WordLoadingUtils;

/**
 * An implementation of {@link Language} interface
 * for Norwegian (bokmaal).
 */
public class Norwegian extends StemmedLanguageBase {

    /**
     * A set of stopwords for this language.
     */
    private final static Set stopwords;
    
    /*
     * Load stopwords from an associated resource.
     */
    static {
        String resourceName = "stopwords.no";
        try {
            stopwords = WordLoadingUtils.loadWordSet(
                    resourceName, Norwegian.class.getResourceAsStream(resourceName));
		} catch (IOException e) {
            throw new RuntimeException("Could not load the required" +
                    "resource: " + resourceName);
		}
    }

    
    /**
     * Public constructor. 
     */
    public Norwegian() {
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
        return WordBasedParserFactory.Default.borrowParser();
	}

	/**
     * @return Language code: <code>no</code>
	 * @see com.dawidweiss.carrot.core.local.linguistic.Language#getIsoCode()
	 */
	public String getIsoCode() {
        return "no";
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
