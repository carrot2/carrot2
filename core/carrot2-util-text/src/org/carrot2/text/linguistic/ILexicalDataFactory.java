package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;

/**
 * Provides instances of {@link ILexicalData} for each language.
 */
public interface ILexicalDataFactory
{
    /**
     * Provide {@link ILexicalData} for a given language. May take 
     * considerable time to read and parse resources from disk. Factories
     * should cache their processed data structures, if possible.
     */
    public ILexicalData getLexicalData(LanguageCode languageCode);
}
