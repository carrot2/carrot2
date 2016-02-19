
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

/**
 * Provides instances of {@link ITokenizer} for each language.
 */
public interface ITokenizerFactory
{
    /**
     * Provide {@link ITokenizer} for a given language.
     */
    public ITokenizer getTokenizer(LanguageCode languageCode);
}
