
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

package org.carrot2.util.tokenizer.languages;


import java.util.*;

import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;

import org.carrot2.core.linguistic.*;
import org.carrot2.util.tokenizer.parser.*;


/**
 * An abstract base class for {@link Language} classes, implementing
 * pooling of tokenizer and stemmer objects.
 * 
 * <p>This class is meant to help implementing {@link Language}
 * objects, it is not obligatory.
 * 
 * @author Dawid Weiss
 * @version $Revision$ 
 */
public abstract class LanguageBase
    implements Language
{
    /** A soft-reference, unbounded pool of tokenizers. */
    private SoftReferenceObjectPool tokenizersPool;

    /** A soft-reference, unbounded stemmers pool. */
    private SoftReferenceObjectPool stemmersPool;

    /** 
     * A set of stopwords for this language. The set
     * contains {@link java.lang.String} entries.
     */
    private Set stopwords;

    /** 
     * Private flag indicating that stemmers factory should not be used.
     * This flag is needed because at first we don't know whether {@link #createStemmerInstance()}
     * is overriden or not. The default stemmers factory returns <code>null</code>
     * which would make the soft pool fall into an endless loop. 
     */
    private boolean stemmerNotUsed;

    /** 
     * A nested class that implements {@link PoolableObjectFactory} interface
     * and returns objects acquired from the overriden abstract
     * {@link #createTokenizerInstance()} method.  
     */
    private final class TokenizersFactory extends BasePoolableObjectFactory {
            public Object makeObject() throws Exception
            {
                return createTokenizerInstance();
            }
        }

    /** 
     * A nested class that implements {@link PoolableObjectFactory} interface
     * and returns objects acquired from the overriden abstract
     * {@link #createStemmerInstance()} method.
     */
    private final class StemmersFactory extends BasePoolableObjectFactory {
            public Object makeObject() throws Exception
            {
                Object instance = createStemmerInstance();
                if (instance == null) {
                    throw new RuntimeException("createStemmerInstance() must not return null.");
                }
                return instance;
            }
        }
        
    /**
     * Default empty constructor.
     */
    public LanguageBase() {
        initialize();
    }

    /**
     * Creates a new instance of {@link LanguageTokenizer} object.
     * The instances returned from this method are pooled for reuse.
     * 
     * <p>Override this method, if a specific implementation of 
     * {@link LanguageTokenizer} is to be used. The default
     * implementation returns an instance of {@link WordBasedParserBase}.
     * 
     * @return An instance of {@link LanguageTokenizer}.
     */
    protected LanguageTokenizer createTokenizerInstance() {
        return WordBasedParserFactory.Default.borrowParser();
    }

    /**
     * Creates a new instance of {@link Stemmer} object.
     * The instances returned from this method are pooled for reuse.
     * 
     * <p>Override this method, if a specific implementation of 
     * {@link Stemmer} is to be used. The default implementation
     * sets a flag in the superclass which causes {@link #borrowTokenizer}
     * to always return <code>null</code>.
     * 
     * @return Overriden methods should return an instance of {@link Stemmer}.
     * <b><code>null</code> must never be returned from this method, or the
     * pool will fall into an endless loop.</b>
     */
    protected Stemmer createStemmerInstance() {
        this.stemmerNotUsed = true;
        return null;
    }

    /**
     * Initializes the pools and factories of this
     * object.
     */
    private final void initialize() {
        // check if createstemmerinstance is overriden by invoking it.
        if (createStemmerInstance() == null) {
            if (!stemmerNotUsed) {
                // overriden method returned null. complain.
                throw new RuntimeException("Overriden createStemmerInstance() must not return null.");
            }
        }
        
        this.tokenizersPool = 
            new SoftReferenceObjectPool(new TokenizersFactory());
        this.stemmersPool = 
            new SoftReferenceObjectPool(new StemmersFactory());
    }

    /**
     * @return Returns a new, or pooled instance of
     * {@link LanguageTokenizer}. 
     * 
     * @see Language#borrowTokenizer()
     */
    public final LanguageTokenizer borrowTokenizer()
    {
        try
        {
            return (LanguageTokenizer) tokenizersPool.borrowObject();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error acquiring a tokenizer from the pool.", e);
        }
    }

    /**
     * Returns the tokenizer object back to the pool. 
     * 
     * @see Language#returnTokenizer(LanguageTokenizer)
     */
    public final void returnTokenizer(LanguageTokenizer tokenizer)
    {
        try
        {
            tokenizersPool.returnObject(tokenizer);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error returning a tokenizer to the pool.", e);
        }
    }

	/**
     * @return Returns a new, or pooled instance of 
     * {@link Stemmer} object.
     * 
	 * @see org.carrot2.core.linguistic.Language#borrowStemmer()
	 */
	public final Stemmer borrowStemmer() {
        try
        {
            if (this.stemmerNotUsed)
                return null;
            else
            	return (Stemmer) stemmersPool.borrowObject();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error acquiring a tokenizer from the pool.", e);
        }
	}

	/** 
     * Returns the borrowed {@link Stemmer} instance back to the pool.
     * 
	 * @see org.carrot2.core.linguistic.Language#returnStemmer(org.carrot2.core.linguistic.Stemmer)
	 */
	public final void returnStemmer(Stemmer stemmer) {
        try
        {
            if (stemmerNotUsed)
                throw new RuntimeException("Stemmer is not used, so this call is probably an error.");
            else
                stemmersPool.returnObject(stemmer);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error returning a tokenizer to the pool.", e);
        }
	}

	/**
	 * Returns the stopwords set for this language. <code>null</code>
	 * (no stopwords) by default, unless set to some other value using
	 * {@link #setStopwords(Set)}.
	 */
	public final Set getStopwords() {
	    return stopwords;
	}
	
	
	/**
	 * Sets stopwords for this language. The argument
	 * <code>stopwords</code> will be returned from
	 * {@link #getStopwords()}.
	 * 
	 * @param stopwords A set of {@link java.lang.String} objects.
	 */
	protected void setStopwords(Set stopwords) {
	    this.stopwords = stopwords;
	}
}
