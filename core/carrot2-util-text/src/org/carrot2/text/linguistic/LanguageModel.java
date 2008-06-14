package org.carrot2.text.linguistic;


/**
 * Linguistic resources and tools dedicated to a given language.
 */
public interface LanguageModel
{
    /**
     * @return Returns <code>true</code> if <code>word</code> is common (meaningless)
     *         in this language. Such words are referred to as "stop words" and are
     *         usually ignored in information retrieval tasks. Depending on the
     *         implementation, <code>word</code> may be lower-cased internally.
     */
    public boolean isCommonWord(CharSequence word);

    /**
     * @return Returns an engine for conflating inflected forms to their dictionary head
     *         form. Stemming is usually a heuristic and is different from lemmatisation.
     *         An empty (identity) stemmer is returned if stemming is not available for
     *         this language. <b>The returned stemmer must be thread safe.</b>
     */
    public Stemmer getStemmer();
    
    /**
     * @return Returns {@link LanguageCode} for this model.
     */
    public LanguageCode getLanguageCode();
}
