
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for working with {@link List}s.
 */
public final class ListUtils
{
    private ListUtils()
    {
    }

    public static <E> ArrayList<E> asArrayList(List<E> list)
    {
        if (list instanceof ArrayList)
        {
            return (ArrayList<E>) list;
        }
        else
        {
            return new ArrayList<E>(list);
        }
    }

    public static int [] asArray(List<Integer> list)
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
