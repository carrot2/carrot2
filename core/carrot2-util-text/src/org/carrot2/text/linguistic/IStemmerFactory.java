
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
 * Provides a stemming engine, typically for a hard-coded language.
 */
public interface IStemmerFactory
{
    /**
     * Creates a new (or reusable, but thread-safe) instance of a stemmer.
     */
    public IStemmer createInstance();
}
