
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

import org.apache.commons.lang.ObjectUtils;

/**
 * An immutable pair of objects.
 */
public class Pair<I, J>
{
    public final I objectA;
    public final J objectB;

    public Pair(I clazz, J parameter)
    {
        this.objectA = clazz;
        this.objectB = parameter;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Pair))
        {
            return false;
        }

        final Pair<?, ?> other = (Pair<?, ?>) obj;

        return ObjectUtils.equals(other.objectA, objectA)
            && ObjectUtils.equals(other.objectB, objectB);
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(objectA) ^ ObjectUtils.hashCode(objectB);
    }
}
