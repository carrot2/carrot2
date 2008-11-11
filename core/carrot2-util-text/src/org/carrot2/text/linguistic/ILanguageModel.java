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

package org.carrot2.text.linguistic;

/**
 * Linguistic resources and tools dedicated to a given language.
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
     *         declared a stop label (meaningless) in this language.
     */
    public boolean isStopLabel(CharSequence formattedLabel);

    /**
     * @return Returns an engine for conflating inflected forms to their dictionary head
     *         form. Stemming is usually a heuristic and is different from lemmatisation.
     *         An empty (identity) stemmer is returned if stemming is not available for
     *         this language. <b>The returned stemmer is not guaranteed to be
     *         thread-safe.</b>
     */
    public IStemmer getStemmer();

    /**
     * @return Returns {@link LanguageCode} for this model.
     */
    public LanguageCode getLanguageCode();
}
