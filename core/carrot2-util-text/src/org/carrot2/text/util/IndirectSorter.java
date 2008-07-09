package org.carrot2.text.util;

import java.util.Comparator;

import bak.pcj.DoubleComparator;
import bak.pcj.IntComparator;

/**
 * Utilities for sorting arrays that rather than swap elements in these arrays, return
 * <code>int []</code> arrays with the actual order and keep the input arrays intact.
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
     * Returns the order of elements in <code>array</code> according to
     * <code>comparator</code>.
     */
    public static <T> int [] sort(T [] array, Comparator<T> comparator)
    {
        int [] order = createOrderArray(array.length);
        quickSort(array, 0, array.length - 1, order, comparator);
        insertionSort(array, 0, array.length - 1, order, comparator);

        return order;
    }

    /**
     * Returns the order of elements in <code>array</code> according to
     * <code>comparator</code>.
     */
    public static int [] sort(int [] array, IntComparator comparator)
    {
        int [] order = createOrderArray(array.length);
        quickSort(array, 0, array.length - 1, order, comparator);
        insertionSort(array, 0, array.length - 1, order, comparator);

        return order;
    }

    /**
     * Returns the order of elements in <code>array</code> according to
     * <code>comparator</code>.
     */
    public static int [] sort(double [] array, DoubleComparator comparator)
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
     * A common helper method for swapping elements of an array.
     */
    private static final void swap(int [] array, int l, int i)
    {
        int temp = array[l];
        array[l] = array[i];
        array[i] = temp;
    }

    /**
     * Internal 2-way Quicksort for {@link Object}s.
     */
    private static <T> void quickSort(T [] array, int l, int r, int [] order,
        Comparator<T> comparator)
    {
        if ((r - l) > M)
        {
            int i = (r + l) >>> 1;

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

    /**
     * Internal insertion sort for {@link Object}s.
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

    /**
     * Internal 2-way Quicksort for <code>int</code>s.
     */
    private static void quickSort(int [] array, int l, int r, int [] order,
        IntComparator intComparator)
    {
        if ((r - l) > M)
        {
            int i = (r + l) >>> 1;

            if (intComparator.compare(array[order[l]], array[order[i]]) > 0)
            {
                swap(order, l, i);
            }
            if (intComparator.compare(array[order[l]], array[order[r]]) > 0)
            {
                swap(order, l, r);
            }
            if (intComparator.compare(array[order[i]], array[order[r]]) > 0)
            {
                swap(order, i, r);
            }

            int j = r - 1;
            swap(order, i, j);
            i = l;
            int v = array[order[j]];
            for (;;)
            {
                while (intComparator.compare(array[order[++i]], v) < 0)
                {
                }
                while (intComparator.compare(array[order[--j]], v) > 0)
                {
                }
                if (j < i)
                {
                    break;
                }
                swap(order, i, j);
            }
            swap(order, i, r - 1);

            quickSort(array, l, j, order, intComparator);
            quickSort(array, i + 1, r, order, intComparator);
        }
    }

    /**
     * Internal insertion sort for <code>int</code>s.
     */
    private static void insertionSort(int [] array, int lo0, int hi0, int [] order,
        IntComparator intComparator)
    {
        for (int i = lo0 + 1; i <= hi0; i++)
        {
            int v = order[i];
            int j = i;
            int t;
            while (j > lo0
                && intComparator.compare(array[t = order[j - 1]], array[v]) > 0)
            {
                order[j] = t;
                j--;
            }
            order[j] = v;
        }
    }

    /**
     * Internal 2-way Quicksort for <code>double</code>s.
     */
    private static void quickSort(double [] array, int l, int r, int [] order,
        DoubleComparator doubleComparator)
    {
        if ((r - l) > M)
        {
            int i = (r + l) >>> 1;

            if (doubleComparator.compare(array[order[l]], array[order[i]]) > 0)
            {
                swap(order, l, i);
            }
            if (doubleComparator.compare(array[order[l]], array[order[r]]) > 0)
            {
                swap(order, l, r);
            }
            if (doubleComparator.compare(array[order[i]], array[order[r]]) > 0)
            {
                swap(order, i, r);
            }

            int j = r - 1;
            swap(order, i, j);
            i = l;
            double v = array[order[j]];
            for (;;)
            {
                while (doubleComparator.compare(array[order[++i]], v) < 0)
                {
                }
                while (doubleComparator.compare(array[order[--j]], v) > 0)
                {
                }
                if (j < i)
                {
                    break;
                }
                swap(order, i, j);
            }
            swap(order, i, r - 1);

            quickSort(array, l, j, order, doubleComparator);
            quickSort(array, i + 1, r, order, doubleComparator);
        }
    }

    /**
     * Internal insertion sort for <code>double</code>s.
     */
    private static void insertionSort(double [] array, int lo0, int hi0, int [] order,
        DoubleComparator doubleComparator)
    {
        for (int i = lo0 + 1; i <= hi0; i++)
        {
            int v = order[i];
            int j = i;
            int t;
            while (j > lo0
                && doubleComparator.compare(array[t = order[j - 1]], array[v]) > 0)
            {
                order[j] = t;
                j--;
            }
            order[j] = v;
        }
    }
}
