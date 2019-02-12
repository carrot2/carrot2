
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

import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.util.attribute.*;

/**
 * A holder for all elements of a language model for a single language used internally by
 * content preprocessing components.
 */
@Bindable
public final class LanguageModel
{
    @Input
    @Processing
    @Attribute(key = "MultilingualClustering.defaultLanguage")
    @Required
    @Level(AttributeLevel.MEDIUM)
    public LanguageCode language;

    public ITokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
    public IStemmerFactory stemmerFactory = new DefaultStemmerFactory();
    public ILexicalDataFactory lexicalDataFactory = new DefaultLexicalDataFactory();

    public IStemmer stemmer;
    public ITokenizer tokenizer;
    public ILexicalData lexicalData;

    public LanguageModel() {
        this(LanguageCode.ENGLISH);
    }

    public LanguageModel(LanguageCode language) {
        this(language, null, null, null);
    }

    public LanguageModel(LanguageCode languageCode,
                  IStemmer stemmer,
                  ITokenizer tokenizer,
                  ILexicalData lexicalData)
    {
        this.language = languageCode;
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.lexicalData = lexicalData;
    }

    public LanguageModel resolve() {
        return new LanguageModel(language,
            stemmerFactory.getStemmer(language),
            tokenizerFactory.getTokenizer(language),
            lexicalDataFactory.getLexicalData(language));
    }

    public boolean usesSpaceDelimiters() {
        return language.usesSpaceDelimiters();
    }
}
