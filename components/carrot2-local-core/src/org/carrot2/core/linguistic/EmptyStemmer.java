/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.linguistic;

/**
 * An empty implementation of {@link Stemmer} that always returns
 * <code>null</code>.
 * 
 * @author Stanislaw Osinski
 */
public class EmptyStemmer implements Stemmer
{
    public static final EmptyStemmer INSTANCE = new EmptyStemmer();

    private EmptyStemmer()
    {

    }

    public String getStem(char [] charArray, int startCharacterIndex, int length)
    {
        return null;
    }
}
