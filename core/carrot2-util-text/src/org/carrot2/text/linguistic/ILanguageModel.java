
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;

/**
 * Linguistic resources and tools dedicated to a given language. Instances of this
 * interface (and their derived resources) are not thread safe.
 */
public interface ILanguageModel
{
    /**
     * @return Returns <code>true</code> if <code>word</code> is common (meaningless) in
     *         this language. Such words are referred to as "stop words" and are usually
     *         ignored in information retrieval tasks. Depending on the implementation,
     *         <code>word</code> may be lower-cased internally.
     */
    public boolean isCommonWord(CharSequence word);

    /**
     * @return Returns <code>true</code> if the <code>formattedLabel</code> has been
     *         declared a stop label (meaningless) in this language. This is a very
     *         low-level tuning method.
     */
    public boolean isStopLabel(CharSequence formattedLabel);

    /**
     * @return Returns an engine for conflating inflected forms to their dictionary head
     *         form. Stemming is usually a heuristic and is different from lemmatisation.
     *         An empty (identity) stemmer is returned if stemming is not available for
     *         this language.
     */
    public IStemmer getStemmer();

    /**
     * @return Return an engine for splitting the input text into individual words
     *         (tokens).
     */
    public ITokenizer getTokenizer();

    /**
     * @return Returns {@link LanguageCode} for this model.
     */
    public LanguageCode getLanguageCode();
}
