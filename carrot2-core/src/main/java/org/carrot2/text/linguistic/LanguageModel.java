
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import org.carrot2.text.analysis.ITokenizer;

/**
 * A holder for all elements of a language model for a single language used internally by
 * content preprocessing components.
 */
public final class LanguageModel
{
    public IStemmer stemmer;
    public ITokenizer tokenizer;
    public ILexicalData lexicalData;

    public LanguageModel(IStemmer stemmer,
                         ITokenizer tokenizer,
                         ILexicalData lexicalData)
    {
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.lexicalData = lexicalData;
    }
}
