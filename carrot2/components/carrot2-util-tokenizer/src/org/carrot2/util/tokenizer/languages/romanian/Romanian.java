
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

package org.carrot2.util.tokenizer.languages.romanian;

import java.io.IOException;
import java.util.Set;

import org.carrot2.core.linguistic.*;
import org.carrot2.stemming.snowball.SnowballStemmersFactory;
import org.carrot2.util.WordLoadingUtils;
import org.carrot2.util.tokenizer.languages.StemmedLanguageBase;
import org.carrot2.util.tokenizer.parser.WordBasedParserFactory;

/**
 * An implementation of {@link Language} interface
 * for the Romanian language.
 * 
 * <p>Requires <code>carrot2-stemmer-snowball</code> for
 * stemming capabilities
 * 
 * @author Dawid Weiss
 */
public class Romanian extends StemmedLanguageBase {

    /**
     * A set of stopwords for this language.
     */
    private final static Set stopwords;
    
    /*
     * Load stopwords from an associated resource.
     */
    static {
        try {
            stopwords = WordLoadingUtils.loadWordSet("stopwords.ro");
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize class: " + e.getMessage());
        }
    }

    
    /**
     * Public constructor. 
     */
    public Romanian() {
        super.setStopwords(stopwords);
    }
    
	/**
     * Creates a new instance of a {@link LanguageTokenizer} for 
     * this language.
     * 
	 * @see org.carrot2.util.tokenizer.languages.StemmedLanguageBase#createTokenizerInstanceInternal()
	 */
	protected LanguageTokenizer createTokenizerInstanceInternal() {
        // TODO: This tokenizer is never returned to the pool, but actually
        // all languages could share the same tokenizer pool
        return WordBasedParserFactory.Default.borrowParser();
	}

	/**
     * @return Language code: <code>en</code>
	 * @see org.carrot2.core.linguistic.Language#getIsoCode()
	 */
	public String getIsoCode() {
        return "ro";
	}

    /** 
     * Return an instance of a stemmer for this language
     * from the Snowball component.
     *  
     * @see org.carrot2.util.tokenizer.languages.LanguageBase#createStemmerInstance()
     */
    protected Stemmer createStemmerInstance() {
        return SnowballStemmersFactory.getInstance(getIsoCode());
    }
}
