
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees;

/**
 * A {@link ISuffixableElement} wrapper around a {@link CharSequence}.
 */
final class SuffixableCharacterSequence implements ISuffixableElement
{
    private final Character [] chars;
    private final String asString;

    public SuffixableCharacterSequence(CharSequence chs)
    {
        this.chars = new Character [chs.length()];
        for (int i = chs.length() - 1; i >= 0; i--)
        {
            chars[i] = chs.charAt(i);
        }
        this.asString = chs.toString();
    }

    public Object get(int index)
    {
        return chars[index];
    }

    public int size()
    {
        return chars.length;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof SuffixableCharacterSequence)
        {
            return asString.equals(((SuffixableCharacterSequence) obj).asString);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return asString.hashCode();
    }
}
