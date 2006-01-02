
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.chilang.util;

import cern.colt.bitvector.BitVector;
import cern.colt.Swapper;
import cern.colt.GenericSorting;
import cern.colt.function.IntComparator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class ArrayUtils {

    private static NumberFormat nf = FormatterFactory.getNumberFormat(4);
    private static final Random RANDOM_GENERATOR = new Random();

    private static final String[] DEFAULT_SEPARATORS = new String[] {"{", ",", "}" };


    private static final StringFormatter DEFAULT_ARRAY_FORMATTER = new ArrayFormatter(DEFAULT_SEPARATORS);

    /**
     * Create String representation of given array using specified Formatter
     * @param array array of objects
     * @param itemFormatter convert object to String (kind of external toString())
     * @return
     */
    public static String toString(Object[] array, StringFormatter itemFormatter) {
        StringBuffer buf = new StringBuffer();
        String sep = "";
        buf.append("{");
        for (int i = 0; i < array.length; i++) {
            buf.append(sep + itemFormatter.toString(array[i]));
            sep = ",";
        }
        buf.append("}");
        return buf.toString();
    }

    /**
     * Return string representation of given array
     * @param array
     * @return
     */
    public static String toString(Object array) {
        if (array == null)
            return "null";
        if (array.getClass().isArray())
            return DEFAULT_ARRAY_FORMATTER.toString(array);
        return array.toString();
//        StringBuffer buf = new StringBuffer();
//        String sep = "";
//        buf.append("{");
//        for (int i = 0; i < array.length; i++) {
//            buf.append(sep + array[i]);
//            sep = ",";
//        }
//        buf.append("}");
//        return buf.toString();

    }

//    public static String toString(double[] arr) {
//        StringBuffer buf = new StringBuffer();
//        String sep = "";
//        buf.append("{");
//        for (int i = 0; i < arr.length; i++) {
//            buf.append(sep + nf.format(arr[i]));
//            sep = ",";
//        }
//        buf.append("}");
//        return buf.toString();
//    }

//    public static String toString(Object[] arr) {
////        StringBuffer buf = new StringBuffer();
////        String sep = "";
////        buf.append("{");
////        for (int i = 0; i < arr.length; i++) {
////            buf.append(sep + arr[i]);
////            sep = ",";
////        }
////        buf.append("}");
////        return buf.toString();
//        return DEFAULT_ARRAY_FORMATTER.toString(arr);
//    }

    /**
     * Calculate mean of the array
     * @param arr
     * @return
     */
    public static double mean(int[] arr) {
        int acc = 0;
        for (int i = 0; i < arr.length; i++) {
            acc += arr[i];
        }
        return (double) acc / (double) arr.length;
    }

    /**
     * Calculate non-0 mean, mean, max, non-zero values of array
     * @param arr
     * @return array[0 - mean of non-zero values, 1 - mean, 2 - max, 3 - number of non-zero values]
     */
    public static double[] characteristic(int[] arr) {
        double[] m3 = new double[4];
        m3[0] = Double.MAX_VALUE;
        m3[2] = Double.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < m3[0])
                m3[0] = arr[i];

            m3[1] += arr[i];

            if (arr[i] > m3[2])
                m3[2] = arr[i];

            if (arr[i] != 0)
                m3[3] += 1;
        }
        m3[1] = m3[1] / m3[3];
        m3[0] = m3[3] / arr.length;
        return m3;
    }

    /**
     * Extract a column from matrix
     * @param colNum
     * @param matrix
     * @return
     */
    public static int[] extractColumn(int colNum, int[][] matrix) {
        int[] column = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][colNum];
        }
        return column;
    }

    /**
     * Create concatenation of given number of array's element starting from given index.
     * Words in concatenation are seperated by specified String.
     * @param arr array of String
     * @param start starting index from which sub-array is extracted
     * @param len length of sub-array to be extracted
     * @return String which is concatentation of array's element from arr[start]...arr[start+len-1]
     */
    public static String concat(String[] arr, int start, int len) {
        StringBuffer buf = new StringBuffer();
        String sep = "";
        for (int i = 0; i < len; i++) {
            buf.append(sep + arr[start + i]);
            sep = " ";
        }
        return buf.toString();
    }

    /**
     * Return indices of elements which values satisfied given filter function
     * @param arr array of int values
     * @param function function to filter out int values
     * @return array of indices
     */
    public static int[] filter(int[] arr, IntFilter function) {
        int[] filtered = new int[arr.length];
        int j = 0;
        for (int i = 0; i < arr.length; i++) {
            if (function.filter(arr[i])) {
                filtered[j++] = i;
            }
        }
        int[] tmp = new int[j];
        System.arraycopy(filtered, 0, tmp, 0, j);
        return tmp;
    }

    /**
     * Extract elements of array given in indices
     * @param arr array
     * @param indices indices of element to be extracted
     * @return array of elements from original array which indices were specified
     */
    public static int[] projection(int[] arr, int[] indices) {
        int[] projected = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            projected[i] = arr[indices[i]];
        }
        return projected;
    }

    /**
     * Copy elements of array which indices in bit vector are set
     * @param arr array of elements
     * @param indices bit vector of indices (selected bit indices are set)
     * @return copy of original array with only selected indices copied (
     */
    public static int[] andCopy(int[] arr, BitVector indices) {
        int[] projected = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            if (indices.getQuick(i))
                projected[i] = arr[i];
        }
        return projected;
    }

    public static String[] projection(String[] arr, int[] indices) {
        String[] projected = new String[indices.length];
        for (int i = 0; i < indices.length; i++) {
            projected[i] = arr[indices[i]];
        }
        return projected;
    }

    public static Collection projection(Object[] arr, int[] indices) {
        Collection projected = new ArrayList();
        for (int i = 0; i < indices.length; i++) {
            projected.add(arr[indices[i]]);
        }
        return projected;
    }


    public static boolean isEqualsIgnoreCase(String[] arr, String[] tst) {
        if (arr.length != tst.length)
            return false;
        for (int i = 0; i < arr.length; i++) {
            if (!arr[i].equalsIgnoreCase(tst[i]))
                return false;
        }
        return true;
    }


    /**
     * Construct a bit vector from its representation as string
     * (where "0" - unset bit, "1" set bit, lenght of string = vector length).
     * For e.g "01011" -> {0,1,0,1,1}
     * @param bitString
     * @return
     */
    public static BitVector constructFromString(String bitString) {
        int length = bitString.length();
        BitVector bits = new BitVector(length);
        for (int i = 0; i < length; i++) {
            if ('1' == bitString.charAt(i))
                bits.putQuick(i, true);
        }
        return bits;
    }

    public static Object[] filter(Object[] array, Filter filter) {
        Object[] filtered = (Object[])java.lang.reflect.Array.newInstance(
                array.getClass().getComponentType(), array.length);
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (filter.accept(array[i]))
                filtered[j++] = array[i];
        }
        if (j < array.length) {
            Object[] tmp = (Object[])java.lang.reflect.Array.newInstance(
                    array.getClass().getComponentType(), j);
            System.arraycopy(filtered, 0, tmp, 0, j);
            return tmp;
        } else {
            return filtered;
        }
    }

    /**
     * Generate array of k UNIQUEs random integer between range [0, max)
     * @param k length of array ( <= max)
     * @param max
     * @return k unique random integer from range [0, max)
     */
    public static int[] randomIntArray(int k, int max) {
        //use hit map, k must not be too large
        //trade memory for speed
        int[] hit = new int[max];
        int[] randSet = new int[k];
        for (int i = 0; i < k; i++) {
            int rand = 0;
            do {
                rand = RANDOM_GENERATOR.nextInt(max);
                //check if rand has been already generated
                if (hit[rand] == 0) {
                    hit[rand] = 1; //mark hit
                    break; //while
                }

            } while (true);
            randSet[i] = rand;
        }
        return randSet;
    }

    /**
     * Get non-zero (set) indices of a bit vector
     * @param vector
     * @return array of indices in the vector where bits are set
     */
    public static int[] getNonZeroIndices(BitVector vector) {
        int size = vector.size();
        int length = vector.cardinality();
        int[] indices = new int[length];
        for (int i = size; --i >= 0;) {
            if (vector.getQuick(i))
                indices[--length] = i;
        }
        return indices;
    }

    /**
     * Get non-zero indices of a vector represented as an int array
     * @param vector
     * @return
     */
    public static int[] getNonZeroIndices(int[] vector) {
        int size = vector.length;
        int[] indices = new int[size];
        int k = 0;
        for (int i = size; --i >= 0;) {
            if (vector[i] != 0)
                indices[k++] = i;
        }
        int[] tmp = new int[k];
        System.arraycopy(indices, 0, tmp, 0, k);
        return tmp;
    }

    /**
     * Get non-zero indices of a vector represented as an double array
     * @param vector
     * @return
     */
    public static int[] getNonZeroIndices(double[] vector) {
        int size = vector.length;
        int[] indices = new int[size];
        int k = 0;
        for (int i = size; --i >= 0;) {
            if (vector[i] != 0.0)
                indices[k++] = i;
        }
        int[] tmp = new int[k];
        System.arraycopy(indices, 0, tmp, 0, k);
        return tmp;
    }


    /**
     * Get non-zero values of a vector.
     *
     * @param vector
     * @return array of non-zero values in order like in the original vector
     * (when iterating from index 0 upward)
     */
    public static double[] getNonZeroValues(double[] vector) {
        double[] tmp = new double[vector.length];
        int k = 0;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] != 0)
                tmp[k++] = vector[i];
        }
        double[] nonzero = new double[k];
        System.arraycopy(tmp, 0, nonzero, 0, k);
        return nonzero;
    }

    /**
     * Sort the given array by sorting its indices array.
     * @param array
     * @return array of indices to access original array in a sorted order
     */
    public static int[] getSortedIndices(final double[] array) {
        int length = array.length;
        final int[] indices = new int[length];
        for (int i=0; i <length; i++)
            indices[i] = i;

        Swapper swapper = new Swapper() {
            public void swap(int a, int b) {
                int tmp = indices[a];
                indices[a] = indices[b];
                indices[b] = tmp;
            }
        };

        IntComparator comparator = new IntComparator() {
            public int compare(int o1, int o2) {
                return array[indices[o1]] >= array[indices[o2]] ? 1 : -1;
            }
        };

        GenericSorting.mergeSort(0, length, comparator, swapper);
        return indices;
    }

    public static double[] getValuesByIndices(double[] values, int[] indices) {
        double[] vals = new double[indices.length];
        for (int i = 0; i < indices.length; i++) {
            vals[i] = values[indices[i]];
        }
        return vals;
    }

    public static int sum(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }
    
    public static double sum(double[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    /**
     * Calculate aggregated sum
     * sum[i] = arr[i] + arr[i+1]+ ...+ a[n]
     * @param arr
     * @return
     */
    public static int[] aggregatedSum(int[] arr) {
        int[] agg = new int[arr.length];
        int sum = 0;
        for (int i = arr.length; --i >= 0; ) {
            sum += arr[i];
            agg[i] = sum;
        }
        return agg;
    }

    public static double[] percent(int total, int[] arr) {
            double[] p = new double[arr.length];
            for (int i = 0; i < p.length; i++) {
                p[i] = (double)arr[i]/total;
            }
            return p;
        }

    /**
     * Normalize vector
     * @param vector
     */
    public static void normalize(double[] vector) {

        double normalizationFactor = 0;
        for (int i=0; i<vector.length; i++) {
            normalizationFactor += vector[i] * vector[i];
        }


        normalizationFactor = Math.sqrt(normalizationFactor);

        for (int i=0; i<vector.length; i++) {
            vector[i] = vector[i] / normalizationFactor;
        }
    }

    /**
     * Find min value
     * @param vector
     * @return
     */
    public static double min(double[] vector) {
        double min = Double.MAX_VALUE;
        for (int i=0; i < vector.length; i++) {
            if (vector[i] < min)
                min = vector[i];
        }
        return min;
    }

    public static int min(int[] vector) {
        int min = Integer.MAX_VALUE;
        for (int i=0; i < vector.length; i++) {
            if (vector[i] < min)
                min = vector[i];
        }
        return min;
    }

    /**
     * Find max value of vector attribute
     * @param vector
     * @return
     */
    public static double max(double[] vector) {
        double max = Double.MIN_VALUE;
        for (int i=0; i < vector.length; i++) {
            if (vector[i] > max)
                max = vector[i];
        }
        return max;
    }

    public static int max(int[] vector) {
        int max = Integer.MIN_VALUE;
        for (int i=0; i < vector.length; i++) {
            if (vector[i] > max)
                max = vector[i];
        }
        return max;
    }

    /**
     * Calculate vector length
     * @param vector
     * @return
     */
    public static double length(double[] vector) {
        double acc = 0;
        for (int i = 0; i<vector.length; i++) {
            acc += vector[i] * vector[i];
        }
        return Math.sqrt(acc);
    }

    /**
     * Find index of greatest value in the array
     * @param vector
     * @return index of maximum value
     */
    public static int maxIndex(int[] vector) {
        int max = Integer.MIN_VALUE;
        int mIndex = 0;
        for (int i=0; i<vector.length; i++) {
            if (vector[i] > max) {
                max = vector[i];
                mIndex = i;
            }
        }
        return mIndex;


    }
}
