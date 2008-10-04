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
        if (set instanceof HashSet)
        {
            return (HashSet<E>) set;
        }
        else
        {
            return new HashSet<E>(set);
        }
    }
}
