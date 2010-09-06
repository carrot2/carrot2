
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

/**
 * Always returns the singleton {@link IdentityStemmer#INSTANCE}.
 */
final class IdentityStemmerFactory implements IStemmerFactory
{
    @Override
    public IStemmer createInstance()
    {
        return IdentityStemmer.INSTANCE;
    }
}
