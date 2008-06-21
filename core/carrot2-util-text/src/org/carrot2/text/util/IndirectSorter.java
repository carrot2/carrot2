package org.carrot2.text.util;

import java.util.Comparator;

/**
 * Utilities for sorting arrays that rather than swap elements in these arrays, returns
 * <code>int []</code> arrays with the actual order and keeps the input arrays intact.
 * Currently a combination of Quicksort and Insertion sort is used, at some point, if
 * better cache characteristics are required, we may want to switch to e.g. merge sort.
 */
public class IndirectSorter
{
    /**
     * Minimum array fragment size to apply quicksort. Smaller arrays are sorted with
     * insertion sort.
     */
    private static final int M = 16;

    /**
     * Returns the order of elements in <code>array</code> according
     */
    public static <T> int [] sort(T [] array, Comparator<T> comparator)
    {
        int [] order = createOrderArray(array.length);
        quickSort(array, 0, array.length - 1, order, comparator);
        insertionSort(array, 0, array.length - 1, order, comparator);

        return order;
    }

    /**
     * Creates the initial order array.
     */
    private static int [] createOrderArray(int size)
    {
        final int [] order = new int [size];
        final int max = order.length;
        for (int i = 0; i < max; i++)
        {
            order[i] = i;
        }
        return order;
    }

    /**
     * Internal 2-way Quicksort.
     */
    private static <T> void quickSort(T [] array, int l, int r, int [] order,
        Comparator<T> comparator)
    {
        if ((r - l) > M)
        {
            int i = (r + l) / 2;

            if (comparator.compare(array[order[l]], array[order[i]]) > 0)
            {
                swap(order, l, i);
            }
            if (comparator.compare(array[order[l]], array[order[r]]) > 0)
            {
                swap(order, l, r);
            }
            if (comparator.compare(array[order[i]], array[order[r]]) > 0)
            {
                swap(order, i, r);
            }

            int j = r - 1;
            swap(order, i, j);
            i = l;
            T v = array[order[j]];
            for (;;)
            {
                while (comparator.compare(array[order[++i]], v) < 0)
                {
                }
                while (comparator.compare(array[order[--j]], v) > 0)
                {
                }
                if (j < i)
                {
                    break;
                }
                swap(order, i, j);
            }
            swap(order, i, r - 1);

            quickSort(array, l, j, order, comparator);
            quickSort(array, i + 1, r, order, comparator);
        }
    }

    private static final void swap(int [] array, int l, int i)
    {
        int temp = array[l];
        array[l] = array[i];
        array[i] = temp;
    }

    /**
     * Internal insertion sort for small arrays.
     */
    private static <T> void insertionSort(T [] array, int lo0, int hi0, int [] order,
        Comparator<T> comparator)
    {
        for (int i = lo0 + 1; i <= hi0; i++)
        {
            int v = order[i];
            int j = i;
            int t;
            while (j > lo0 && comparator.compare(array[t = order[j - 1]], array[v]) > 0)
            {
                order[j] = t;
                j--;
            }
            order[j] = v;
        }
    }
}
