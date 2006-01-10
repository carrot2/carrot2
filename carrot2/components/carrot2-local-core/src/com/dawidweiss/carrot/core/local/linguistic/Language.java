
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

package com.dawidweiss.carrot.core.local.linguistic;

import java.util.Set;


/**
 * A language object encompasses a factory (pool) of tokenizer  objects ({@link
 * LanguageTokenizer} interface).
 * 
 * <p>
 * It temporarily also exposes instances of stemmer interface  ({@link
 * com.dawidweiss.carrot.core.local.linguistic.Stemmer}), but acquiring
 * stemmers directly should be avoided and replaced with tokenizer use.
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$ 
 */
public interface Language  {
    /**
     * Returns ISO language code, as defined in <b>ISO-639</b> standard. <a
     * href="http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt">http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt</a>
     *
     * @return Returns a two-letter ISO language code.
     * 
     * @see java.util.Locale
     */
    public String getIsoCode();

    /**
     * @return Returns a {@link LanguageTokenizer} instance for this language.
     *         The instance must be returned to the pool when no longer needed
     *         (as soon as possible).
     *
     * @see #returnTokenizer(LanguageTokenizer)
     */
    public LanguageTokenizer borrowTokenizer();

    /**
     * Returns an instance of the {@link LanguageTokenizer} interface
     * previously acquired using {@link #borrowTokenizer()} back to the pool.
     *
     * @param tokenizer The tokenizer acquired from {@link #borrowTokenizer()}.
     * 
     * @see #borrowTokenizer()
     */
    public void returnTokenizer(LanguageTokenizer tokenizer);
    
    /**
     * Returns a stemmer for this language.
     * 
     * <p>
     * <b>This method will most probably  be marked as deprecated and removed
     * in the future</b>.
     * </p>
     * 
     * @return Returns an instance of {@link
     *         com.dawidweiss.carrot.core.local.linguistic.Stemmer} interface
     *         for this language, or <code>null</code> if the language has no
     *         associated stemmer. The instance must be returned back  to the
     *         pool using {@link #returnStemmer(Stemmer)}.
     */
    public Stemmer borrowStemmer();
    
    /**
     * Returns an instance of the {@link Stemmer} interface previously acquired
     * using {@link #borrowStemmer()} back to the pool.
     *
     * @param stemmer The stemmer acquired from {@link #borrowStemmer()}.
     * 
     * @see #borrowStemmer()
     */
    public void returnStemmer(Stemmer stemmer);
    
    /**
     * Returns a {@link java.util.Set} of {@link java.lang.String} objects that
     * should be considered "stopwords" in this language.
     * 
     * <p>
     * Stopwords are terms that are so common that their value as topic
     * determiners is very low. In Information Retrieval these words are
     * usually removed from the input, or ignored. Interestingly, there are
     * contradictory opinions on the importance of stopwords removal,
     * especially if for applications where large volumes of data is
     * available.  <b>This method will most probably  be marked as deprecated
     * and removed in the future</b>.
     * </p>
     * 
     * @return Returns an instance of {@link java.util.Set}, or
     *         <code>null</code> if no stopwords are available for this
     *         language.
     */
    public Set getStopwords();
}
