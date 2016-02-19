
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

package org.carrot2.util;

/**
 * A number of useful methods for working with {@link CharSequence}s.
 */
public final class CharSequenceUtils
{
    /**
     * Converts a {@link CharSequence} into a <code>char []</code> array.
     */
    public static char [] toCharArray(CharSequence charSequence)
    {
        char [] array = new char [charSequence.length()];
        for (int i = 0; i < charSequence.length(); i++)
        {
            array[i] = charSequence.charAt(i);
        }
        return array;
    }

    private CharSequenceUtils()
    {
    }
}
