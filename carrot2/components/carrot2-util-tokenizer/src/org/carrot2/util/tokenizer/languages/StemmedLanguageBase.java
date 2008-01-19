
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

package org.carrot2.util.tokenizer.languages;

import java.util.Locale;

import org.carrot2.core.linguistic.*;


/**
 * A utility abstract class that helps creating languages that have
 * an associated {@link Stemmer} object. This class will automatically
 * attach stems to all tokens produced by tokenizers returned from
 * {@link #createTokenizerInstanceInternal()} method. Subclasses
 * must override {@link LanguageBase#createStemmerInstance()} method
 * and return a reference to a {@link Stemmer} object when requested.
 */
public abstract class StemmedLanguageBase extends LanguageBase {
    /**
     * Creates a new tokenizer using the internal
     * {@link #createTokenizerInstanceInternal()} method and
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
                    new Locale(getIsoCode()), createTokenizerInstanceInternal(), stemmerInstance, getStopwords());
        } else {
            return new StemmingTokenizerWrapper(
                    new Locale(getIsoCode()), createTokenizerInstanceInternal(), stemmerInstance);
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
