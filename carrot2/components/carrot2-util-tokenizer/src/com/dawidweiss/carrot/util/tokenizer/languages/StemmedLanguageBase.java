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
package com.dawidweiss.carrot.util.tokenizer.languages;

import com.dawidweiss.carrot.core.local.linguistic.LanguageTokenizer;
import com.dawidweiss.carrot.core.local.linguistic.Stemmer;


/**
 * A utility abstract class that helps creating languages that have
 * an associated {@link Stemmer} object. This class will automatically
 * attach stems to all tokens produced by tokenizers returned from
 * {@link #createTokenizerInstanceInternal()} method. Subclasses
 * must override {@link LanguageBase#createStemmerInstance()} method
 * and return a reference to a {@link Stemmer} object when requested.
 * 
 * @author Dawid Weiss
 * @version $Revision$ 
 */
public abstract class StemmedLanguageBase extends LanguageBase {
    
    /**
     * An empty constructor, no stopword-marking in performed
     * for TypedTokens.
     */
    public StemmedLanguageBase() {
        super();
    }

    /**
     * Creates a new tokenizer using the internal
     * {@link createTokenizerInstanceInternal()} method and
     * wraps it with a tokenizer that applies stemming to
     * the returned tokens (or rather: their images). 
     * 
     * <p>The stemmer associated with the returned
     * tokenizer is acquired using {@link #borrowStemmer()} method
     * and is never returned back to the pool.
     * 
     * @see #createTokenizerInstanceInternal()
     * @throws RuntimeException if {@link Language#borrowStemmer()} method
     * returned <code>null</code>.
     */
    protected final LanguageTokenizer createTokenizerInstance() {
        Stemmer stemmerInstance = this.borrowStemmer();
        if (stemmerInstance==null)
            throw new RuntimeException("borrowStemmer() must not return null.");
        if (getStopwords() != null) {
            return new StemmingTokenizerWrapper(
                    createTokenizerInstanceInternal(), stemmerInstance, getStopwords());
        } else {
            return new StemmingTokenizerWrapper(
                    createTokenizerInstanceInternal(), stemmerInstance);
        }
    }

    /**
     * @return Should return a new instance of {@link LanguageTokenizer},
     * that produces tokens of type {@link MutableStemmedToken}.
     * 
     * @see MutableStemmedToken
     * @see #createTokenizerInstance()
     */
    protected abstract LanguageTokenizer createTokenizerInstanceInternal();
}
