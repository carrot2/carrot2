
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

import java.util.*;

/**
 * Utility methods for working with {@link Set}s.
 */
public final class SetUtils
{
    private SetUtils()
    {
    }

    public static <E> HashSet<E> asHashSet(Set<E> set)
    {
        if (HashSet.class.isInstance(set))
        {
            return (HashSet<E>) set;
        }
        else
        {
            return new HashSet<E>(set);
        }
    }
}
