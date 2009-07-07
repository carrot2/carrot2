
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

import cern.colt.Arrays;

/**
 * A {@link ISuffixableElement} wrapper around a {@link CharSequence}.
 */
final class SuffixableTermSequence implements ISuffixableElement
{
    private final Object [] terms;

    public SuffixableTermSequence(Object [] terms)
    {
        this.terms = terms;
    }

    public Object get(int index)
    {
        return terms[index];
    }

    public int size()
    {
        return terms.length;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof SuffixableTermSequence)
        {
            return toString().equals(((SuffixableTermSequence) obj).toString());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public String toString()
    {
        return Arrays.toString(terms);
    }
}
