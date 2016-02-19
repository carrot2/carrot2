
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
 * Utility methods for working with {@link List}s.
 */
public final class ListUtils
{
    private ListUtils()
    {
    }

    public static <E> ArrayList<E> asArrayList(Collection<E> list)
    {
        if (ArrayList.class.isInstance(list))
        {
            return (ArrayList<E>) list;
        }
        else
        {
            return new ArrayList<E>(list);
        }
    }

    public static int [] asArray(Collection<Integer> list)
    {
        final int [] result = new int [list.size()];
        int index = 0;
        for (Integer integer : list)
        {
            result[index++] = integer;
        }
        return result;
    }
}
