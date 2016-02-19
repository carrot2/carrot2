
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
import org.carrot2.util.annotations.ThreadSafe;

/**
 * An {@link IStemmerFactory} implementation that returns {@link IdentityStemmer}s for all
 * supported languages.
 */
@ThreadSafe
public final class IdentityStemmerFactory implements IStemmerFactory
{
    private static final IdentityStemmer INSTANCE = new IdentityStemmer();

    @Override
    public IStemmer getStemmer(LanguageCode languageCode)
    {
        return INSTANCE;
    }
}
