
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.util.factory.CachedInstanceFactoryDecorator;
import org.carrot2.util.factory.IFactory;

/**
 * A holder for all elements of a language model for a single language used internally by
 * content preprocessing components.
 */
public final class LanguageModel
{
    private final LanguageCode languageCode;
    private final IFactory<IStemmer> stemmerFactory;
    private final IFactory<ITokenizer> tokenizerFactory;
    private final IFactory<ILexicalData> lexicalDataFactory;

    LanguageModel(LanguageCode languageCode, IFactory<IStemmer> stemmerFactory,
        IFactory<ITokenizer> tokenizerFactory, IFactory<ILexicalData> lexicalDataFactory)
    {
        this.languageCode = languageCode;
        this.stemmerFactory = new CachedInstanceFactoryDecorator<IStemmer>(stemmerFactory);
        this.tokenizerFactory = new CachedInstanceFactoryDecorator<ITokenizer>(
            tokenizerFactory);
        this.lexicalDataFactory = new CachedInstanceFactoryDecorator<ILexicalData>(
            lexicalDataFactory);
    }

    public static LanguageModel create(
        final LanguageCode languageCode,
        final IStemmerFactory stemmerFactory, 
        final ITokenizerFactory tokenizerFactory,
        final ILexicalDataFactory lexicalDataFactory)
    {
        // TODO: we could try to get rid of this extra layer of indirection here:
        // eagerly create instances of language model elements and keep references
        // to them rather than their factories. I'm not sure if the .NET API
        // would work correctly in that case though.
        return new LanguageModel(languageCode, new IFactory<IStemmer>()
        {
            @Override
            public IStemmer createInstance()
            {
                return stemmerFactory.getStemmer(languageCode);
            }
        }, new IFactory<ITokenizer>()
        {
            @Override
            public ITokenizer createInstance()
            {
                return tokenizerFactory.getTokenizer(languageCode);
            }
        }, new IFactory<ILexicalData>()
        {
            @Override
            public ILexicalData createInstance()
            {
                return lexicalDataFactory.getLexicalData(languageCode);
            }
        });
    }

    public LanguageCode getLanguageCode()
    {
        return languageCode;
    }

    public ILexicalData getLexicalData()
    {
        return lexicalDataFactory.createInstance();
    }

    public IStemmer getStemmer()
    {
        return stemmerFactory.createInstance();
    }

    public ITokenizer getTokenizer()
    {
        return tokenizerFactory.createInstance();
    }
}
