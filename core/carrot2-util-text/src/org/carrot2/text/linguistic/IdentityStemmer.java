
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

/**
 * An implementation of {@link IStemmer} that always returns <code>null</code> from
 * {@link #stem(CharSequence)}.
 */
public final class IdentityStemmer implements IStemmer
{
    public CharSequence stem(CharSequence word)
    {
        return null;
    }
}
