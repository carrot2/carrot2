package com.chilang.util;

import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Wrap array of double values offering sorting capabalities for indices
 */
public class ArrayIndexSorter implements Comparator {


    protected double[] values;
    protected boolean ascending = true;

    /**
     * Construct primive
     *
     * @param values
     */
    public ArrayIndexSorter(double[] values) {
        this.values = values;
    }


    public boolean equals(Object obj) {
        return false;
    }

    public int compare(Object o1, Object o2) {
        Integer a = (Integer) o1, b = (Integer) o2;
        return (values[a.intValue()]) > (values[b.intValue()]) ?
                (ascending ? -1 : 1) :
                (ascending ? 1 : -1);
    }

    public int[] getNonZeroIndices() {
        return ArrayUtils.getNonZeroIndices(values);
    }

    public int[] getSortedNonZeroIndices(boolean ascending) {
        int[] nonZero = getNonZeroIndices();
        sort(nonZero, ascending);
        return nonZero;
    }

    /**
     * Sort indices regarding values in the array.
     * @param indices indices to be sorted
     * @param ascending indicate sort order
     */
    public void sort(int[] indices, boolean ascending) {
        this.ascending = ascending;
        List indexList = intArrayAsList(indices);

        Collections.sort(indexList, this);
        int[] tmp = listAsIntArray(indexList);
        System.arraycopy(tmp, 0, indices, 0, tmp.length);
    }

    /** Wrap primitive array in a list */
    private static List intArrayAsList(int[] arr) {
        List list = new ArrayList();
        for (int i = 0; i < arr.length; i++) {
            list.add(new Integer(arr[i]));
        }
        return list;
    }

    /** convert Integer list back to array */
    private static int[] listAsIntArray(List list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = ((Integer)list.get(i)).intValue();
        }
        return arr;
    }
}
