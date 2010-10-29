
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
 * Linguistic resources and tools dedicated to a given language. 
 * 
 * <p>A single instance a language model may return <b>the same</b> stemmer, tokenizer
 * and lexical resource instance. These instances are not thread safe and should
 * not be shared.</p>
 */
public interface ILanguageModel
{
    /**
     * Returns the stemmer associated with this language model.
     */
    public IStemmer getStemmer();

    /**
     * Returns the tokenizer associated with this language model.
     */
    public ITokenizer getTokenizer();

    /**
     * Returns auxiliary lexical data associated with this language model.
     */
    public ILexicalData getLexicalData();

    /**
     * The language code of this model.
     */
    public LanguageCode getLanguageCode();
}
