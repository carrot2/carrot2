
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.util.factory.IFactory;

/**
 * Language model backed by factories.
 */
final class DefaultLanguageModel implements ILanguageModel
{
    private final LanguageCode languageCode;
    private final IFactory<IStemmer> stemmerFactory;
    private final IFactory<ITokenizer> tokenizerFactory;
    private final IFactory<ILexicalData> lexicalDataFactory;

    DefaultLanguageModel(LanguageCode languageCode,
                          IFactory<IStemmer> stemmerFactory,
                          IFactory<ITokenizer> tokenizerFactory,
                          IFactory<ILexicalData> lexicalDataFactory)
    {
        this.languageCode = languageCode;
        this.stemmerFactory = stemmerFactory;
        this.tokenizerFactory = tokenizerFactory;
        this.lexicalDataFactory = lexicalDataFactory;
    }

    @Override
    public LanguageCode getLanguageCode()
    {
        return languageCode;
    }
    
    @Override
    public ILexicalData getLexicalData()
    {
        return lexicalDataFactory.createInstance();
    }

    @Override
    public IStemmer getStemmer()
    {
        return stemmerFactory.createInstance();
    }
    
    @Override
    public ITokenizer getTokenizer()
    {
        return tokenizerFactory.createInstance();
    }
}