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
