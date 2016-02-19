
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
