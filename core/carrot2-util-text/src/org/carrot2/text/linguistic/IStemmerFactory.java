
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
 * Provides instances of {@link IStemmer} for each language.
 */
public interface IStemmerFactory
{
    /**
     * Provide {@link IStemmer} for a given language.
     */
    public IStemmer getStemmer(LanguageCode languageCode);
}
