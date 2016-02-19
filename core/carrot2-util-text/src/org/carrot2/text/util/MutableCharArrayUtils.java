
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

package org.carrot2.text.util;

import org.carrot2.util.CharArrayUtils;

/**
 * Various utility methods operating on a {@link MutableCharArray}.
 */
public final class MutableCharArrayUtils
{
    /**
     * Convert to lower case the <code>source</code> array and save the result into the
     * <code>result</code> array. If the result array is too small to accommodate the
     * result, its buffer will be reallocated.
     * 
     * @param source
     * @param result
     * @return Returns <code>true</code> if at least one character was changed between
     *         <code>source</code> and <code>result</code>. <code>false</code> indicates
     *         an identical copy.
     */
    public static boolean toLowerCase(MutableCharArray source, MutableCharArray result)
    {
        char [] buffer = result.getBuffer();

        final int length = source.length();
        if (buffer.length < length)
        {
            buffer = new char [length];
        }

        final boolean changed = CharArrayUtils.toLowerCase(source.getBuffer(), buffer,
            source.getStart(), source.length());
        result.reset(buffer, 0, length);

        return changed;
    }

}
