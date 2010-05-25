
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.Arrays;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntStack;
import com.carrotsearch.hppc.cursors.IntIntCursor;

/**
 * Sparse array encoding utilities. Sparse means an index and its value are kept
 * in an array as a pair.
 */
public final class SparseArray
{
    /**
     * An empty <code>int []</code>. 
     */
    private static final int [] EMPTY_INT_ARRAY = new int [0];

    /**
     * Convert a list of documents to sparse document-count representation.
     */
    public static int [] toSparseEncoding(IntStack documents)
    {
        if (documents.size() == 0)
            return EMPTY_INT_ARRAY;

        // For smaller arrays, count using sorting.
        if (documents.size() < 1000)
        {
            return toSparseEncodingBySort(documents);
        }
        else
        {
            return toSparseEncodingByHash(documents);
        }
    }

    /**
     * Convert to sparse encoding using a hash map.
     */
    public static int [] toSparseEncodingByHash(IntStack documents)
    {
        final IntIntOpenHashMap map = new IntIntOpenHashMap();

        final int toIndex = documents.size();
        final int [] buffer = documents.buffer;
        for (int i = 0; i < toIndex; i++)
        {
            map.putOrAdd(buffer[i], 1, 1);
        }

        return hashToKeyValuePairs(map);
    }

    /*
     * 
     */
    private static int [] hashToKeyValuePairs(IntIntOpenHashMap map)
    {
        final int [] result = new int [map.size() * 2];
        int k = 0;
        for (IntIntCursor c : map)
        {
            result[k++] = c.key;
            result[k++] = c.value;
        }
        return result;
    }

    /**
     * Convert to sparse encoding using sorting and counting.
     */
    public static int [] toSparseEncodingBySort(IntStack documents)
    {
        Arrays.sort(documents.buffer, 0, documents.size());
        final int [] result = new int [2 * countUnique(documents.buffer, 0, documents.size())];

        final int fromIndex = 0;
        final int toIndex = documents.size();
        final int [] buffer = documents.buffer;

        int doc = buffer[fromIndex];
        int count = 1;
        int k = 0;
        for (int i = fromIndex + 1; i < toIndex; i++)
        {
            final int newDoc = buffer[i];
            if (newDoc != doc)
            {
                result[k++] = doc;
                result[k++] = count;
                count = 0;
                doc = newDoc;
            }
            count++;
        }
        if (k < result.length)
        {
            result[k++] = doc;
            result[k++] = count;
        }
        assert k == result.length;
        return result;
    }

    /**
     * Count unique values in the sorted array.
     */
    public static int countUnique(int [] buffer, int fromIndex, int toIndex)
    {
        int unique = 0;
        if (fromIndex < toIndex)
        {
            int val = buffer[fromIndex];
            unique++;
            for (int i = fromIndex + 1; i < toIndex; i++)
            {
                final int j = buffer[i];
                assert j >= val : "Not sorted as expected.";
                if (val != j)
                {
                    unique++;
                    val = j;
                }
            }
        }
        return unique;
    }

    /**
     * Merge data from one or more sparse arrays.
     */
    public static int [] mergeSparseArrays(Iterable<int []> source)
    {
        final IntIntOpenHashMap m = new IntIntOpenHashMap();
        for (int[] list : source)
        {
            final int max = list.length;
            for (int i = 0; i < max; i += 2)
            {
                final int v = list[i + 1];
                m.putOrAdd(list[i], v, v);
            }
        }

        return hashToKeyValuePairs(m);
    }
}
