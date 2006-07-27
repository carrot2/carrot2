
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.util;

import cern.colt.bitvector.BitVector;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;
import hep.aida.bin.DynamicBin1D;

import java.util.Random;

/**
 * Utilities for matrix manipulation
 */
public class MatrixUtils {


    private static final Random RANDOM = new Random();

    /**
     * Transpose matrix (m x n)
     * @param matrix
     * @return transposed matrix (n x m)
     */
    public static int[][] transpose(int[][] matrix) {
        int[][] transposed = new int[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    /**
     * Transpose matrix (m x n)
     * @param matrix
     * @return transposed matrix (n * m)
     */
    public static double[][] transpose(double[][] matrix) {
        double[][] transposed = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }


    public static int[][] multiply3(int[][] m1, int[][] m2) {
        int rows1 = m1.length;
        int cols1 = m1[0].length;
        int rows2 = m2.length;
        int cols2 = m2[0].length;
        if (cols1 != rows2)
            throw new IllegalArgumentException("Matrix dimension mismatch " + rows1 + " * " + cols1 + ", " + m2.length + " * " + cols2);

        int[][] m = new int[rows1][cols2];

        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols2; j++) {
                int s = 0;
                for (int k = 0; k < cols1; k++) {
                    s += m1[i][k] * m2[k][j];
                }
                m[i][j] = s;
            }
        }
        return m;
    }

    public static int[][] multiply2(int[][] m1, int[][] m2) {
        int rows1 = m1.length;
        int cols1 = m1[0].length;
        int rows2 = m2.length;
        int cols2 = m2[0].length;
        if (cols1 != rows2)
            throw new IllegalArgumentException("Matrix dimension mismatch " + rows1 + " * " + cols1 + ", " + m2.length + " * " + cols2);

        int[][] m = new int[rows1][cols2];

        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols2; j++) {
                int s = 0;
                for (int k = 0; k < cols1; k++) {
                    s += m1[i][k] * m2[k][j];
                }
                m[i][j] = s;
            }
        }
        return m;
    }

    /**
     * Multiply two matrices. Matrices should have proper dimension
     * @param m1 matrix of size m * n
     * @param m2 matrix of size n * k
     * @return multiplication of two matrices
     */
    public static int[][] multiply(int[][] m1, int[][] m2) {
        int rows1 = m1.length;
        int cols1 = m1[0].length;
        int rows2 = m2.length;
        int cols2 = m2[0].length;
        if (cols1 != rows2)
            throw new IllegalArgumentException("Matrix dimension mismatch " + rows1 + " * " + cols1 + ", " + m2.length + " * " + cols2);

        int[][] m = new int[rows1][cols2];

        for (int i = rows1; --i >= 0;) {
            for (int j = cols2; --j >= 0;) {
                int s = 0;
                for (int k = cols1; --k >= 0;) {
                    s += m1[i][k] * m2[k][j];
                }
                m[i][j] = s;
            }
        }
        return m;
    }


    /**
     * Return string representation of given matrix.
     * (for display output)
     * @param m
     */
    public static String toString(int[][] m) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < m.length; i++) {
            b.append(ArrayUtils.toString(m[i]) + "\n");
        }
        return b.toString();
    }

    /**
     * Return string representation of non-zero indices of given matrix
     * @param m
     */
    public static String nonZeroIndicesAsString(int[][] m) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < m.length; i++) {
            int length = m[i].length;
            int[] indices = new int[length];
            int k = 0;
            for (int j = 0; j < length; j++) {
                if (m[i][j] != 0)
                    indices[k++] = j;
            }
            int[] tmp = new int[k];
            System.arraycopy(indices, 0, tmp, 0, k);
            b.append(ArrayUtils.toString(tmp) + "\n");
        }
        return b.toString();
    }

    /**
     * Return string representation of given matrix.
     * @param m
     */
    public static String toString(double[][] m) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < m.length; i++) {
            b.append(ArrayUtils.toString(m[i]) + "\n");
        }
        return b.toString();
    }

    /**
     * Normalize all rows of the matrix
     * @param m
     */
    public static void normalizeRows(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            ArrayUtils.normalize(m[i]);
        }
    }

    /**
     * Generate random matrix of rows * colums with value from range (0..maxValue)
     * @param rows
     * @param cols
     * @param maxValue
     */
    public static int[][] generateRandom(int rows, int cols, int maxValue) {
        Random rand = new Random(System.currentTimeMillis());
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = rand.nextInt(maxValue);
            }

        }
        return matrix;
    }

    /**
     * Return a vector that is an aggregated sum of selected rows
     * @param matrix
     * @param selectedRowIndices indices of selected rows
     */
    public static int[] sumSelectedRows(int[][] matrix, int[] selectedRowIndices) {
        int cols = matrix[0].length;
        int selectedRows = selectedRowIndices.length;
        int[] aggr = new int[cols];
        for (int i = 0; i < selectedRows; i++) {
            for (int j = 0; j < cols; j++) {
                aggr[j] += matrix[selectedRowIndices[i]][j];
            }
        }
        return aggr;
    }

    /**
     * Count non-zero entries from selected rows of matrix
     * @param matrix
     * @param selectedRowIndices
     * @return array of non-zero entries for each of selected row
     */
    public static int[] countNonZeroesFromSelectedRows(int[][] matrix, int[] selectedRowIndices) {
        int cols = matrix[0].length;
        int selectedRows = selectedRowIndices.length;
        int[] aggr = new int[cols];
        for (int i = 0; i < selectedRows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[selectedRowIndices[i]][j] > 0)
                    aggr[j]++;
            }
        }
        return aggr;
    }

    /**
     * Count number of non-zero entries
     * @param matrix
     */
    public static int countNonZeroes(int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int nonZeroes = 0;
        for (int i = rows; --i >= 0;) {
            for (int j = cols; --j >= 0;) {
                if (matrix[i][j] > 0)
                    nonZeroes++;
            }
        }
        return nonZeroes;
    }

    /**
     * Return a vector that is an aggregated sum of all rows in the matrix
     * @param matrix
     */
    public static int[] sumRows(int[][] matrix) {
        int cols = matrix[0].length;
        int rows = matrix.length;
        int[] aggr = new int[cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                aggr[j] += matrix[i][j];
            }
        }
        return aggr;
    }


    /**
     * Find max value for each columns
     * @param matrix
     * @return values that is max for each matrix columns
     */
    public static int[] maxByColumns(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[] maxes = new int[cols];

        for (int i = 0; i < maxes.length; i++) {
            maxes[i] = Integer.MIN_VALUE;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] > maxes[j])
                    maxes[j] = matrix[i][j];
            }
        }
        return maxes;
    }

    /**
     * Extension of cern.colt.matrix.doublealgo.Statistic.distance (extends to support two matrix)
     *
     * Constructs and returns the distance matrix of the two matrix (m rows * n cols,  k rows * n cols).
     * The distance matrix is m * k consisting of distance coefficients.
     * The cell[i,j] of the resulting matrix contain distance of vector i
     * (represented as i-th rows in the first matrix) and vector j
     * (represented as j-th rows in the second matrix).
     *
     * @param a m * n matrix; a rows holds the values of a given variable (vector).
     * @param b k * n matrix; a rows holds the values of a given variable (vector).
     * @param distanceFunction (EUCLID, CANBERRA, ..., or any user defined distance function operating on two vectors).
     * @return the distance matrix (<tt>m x k matrix</tt>).
     */
    public static DoubleMatrix2D distance(DoubleMatrix2D a, DoubleMatrix2D b, Statistic.VectorVectorFunction distanceFunction) {
        int rows = a.rows(), cols = b.rows();
        DoubleMatrix2D distance = new cern.colt.matrix.impl.DenseDoubleMatrix2D(rows, cols);



        // work out all permutations
        for (int i = rows; --i >= 0;) {
            for (int j = cols; --j >= 0;) {
                distance.setQuick(i, j, distanceFunction.apply(a.viewRow(i), b.viewRow(j)));
            }
        }
        return distance;
    }


    /**
     * Apply (calculate) a value out from each column of the matrix.
     * This is like applying VectorFunction for each matrix column
     * (columns of underlying matrix remains unchanged)
     * The results is a vector (DoubleMatrix1D) with size of matrix's rows - n.
     * For aggregation of rows, use matrix.viewDice()
     * @param matrix m * n matrix
     * @param f function to be applied on each column to return single value
     * @return the vector of length n
     */
    public static DoubleMatrix1D apply(DoubleMatrix2D matrix, VectorFunction f) {
        DoubleMatrix1D results = DoubleFactory1D.dense.make(matrix.columns());
        for (int i = matrix.columns(); --i >= 0;) {
            results.setQuick(i, f.apply(matrix.viewColumn(i)));
        }
        return results;
    }

    /**
     * Interface for functions on vector which return single value
     */
    public static interface VectorFunction {

        /**
         * Apply function on specified vector
         * @param vector
         * @return result of function applied on given vector
         */
        public double apply(DoubleMatrix1D vector);
    }


    /**
     * Calculate average value of a vector :
     *   average = SUM(vector[i])/ vector.size()
     */
    public static final VectorFunction average = new VectorFunction() {
        public double apply(DoubleMatrix1D vector) {
            double aggr = 0;
            for (int i = vector.size(); --i >= 0;) {
                aggr += vector.getQuick(i);
            }
            return aggr / vector.size();
        }
    };


    /**
     * Function converting in some way double value to int
     */
    public static interface DoubleToIntFunction {

        /**
         * Apply and return int value for a double argument
         * @param value
         */
        public int apply(double value);
    }


    /**
     * Convert a double matrix to a int matrix using given function.
     * Function f is applied to every cell of matrix and the result store in returned int matrix
     * @param matrix double matrix
     * @param f conversion function
     * @return int matrix
     */
    public static int[][] convert(double[][] matrix, DoubleToIntFunction f) {
        int rows = matrix.length, cols = matrix[0].length;
        int[][] result = new int[rows][cols];
        for (int i = rows; --i >= 0;) {
            for (int j = cols; --j >= 0;) {
                result[i][j] = f.apply(matrix[i][j]);
            }
        }
        return result;
    }

    /**
     * Generate random matrix of given size
     * @param rows
     * @param cols
     */
    public static double[][] randomMatrix(int rows, int cols) {
        double[][] rs = new double[rows][cols];
        for (int i = rows; --i >= 0;) {
            for (int j = cols; --j >= 0;) {
                rs[i][j] = RANDOM.nextDouble();
            }
        }
        return rs;
    }

    /**
     * Functions that takes integer argument and return boolean value
     */
    public static interface IntToBooleanFunction {

        /**
         * Apply function to argument
         * @param argument
         */
        public boolean apply(int argument);
    }

    /**
     * Function select (return <code>true</code>) values over given threshold
     */
    public static class IntThresholdFilter implements IntToBooleanFunction {
        protected int threshold;

        public IntThresholdFilter(int threshold) {
            this.threshold = threshold;
        }

        public boolean apply(int argument) {
            return argument > threshold;
        }

    }

    /**
     * Function that takes double argument and return boolean value
     */
    public static interface DoubleToBooleanFunction {

        /**
         * Apply function to argumnet
         * @param argument
         */
        public boolean apply(double argument);
    }

    /**
     * Function select (return <code>true</code>) values over given threshold
     */
    public static class DoubleThresholdFilter implements DoubleToBooleanFunction {
        protected double threshold;

        public DoubleThresholdFilter(double threshold) {
            this.threshold = threshold;
        }

        public boolean apply(double argument) {
            return argument > threshold;
        }

    }

    /**
     * Convert a int matrix to a bit matrix represented as an array ot bit vectors.
     * Each cell of the int matrix is converted to a boolean value using a conversion function
     * @param matrix int matrix (m * n)
     * @param f conversion function
     * @return bit matrix represented by an array of m bit vectors (of length n)
     */
    public static BitVector[] convert(int[][] matrix, IntToBooleanFunction f) {
        int rows = matrix.length, size = matrix[0].length;
        BitVector[] rs = new BitVector[rows];
        for (int i = rows; --i >= 0;) {
            rs[i] = new BitVector(size);
            for (int j = size; --j >= 0;) {
                rs[i].putQuick(j, f.apply(matrix[i][j]));

            }
        }
        return rs;
    }

    /**
     * Convert a double matrix to a bit matrix represented as an array ot bit vectors.
     * Each cell of the double matrix is converted to a boolean value using a conversion function
     * @param matrix double matrix (m * n)
     * @param f conversion function
     * @return bit matrix represented by an array of m bit vectors (of length n)
     */
    public static BitVector[] convert(double[][] matrix, DoubleToBooleanFunction f) {
        int rows = matrix.length, size = matrix[0].length;
        BitVector[] rs = new BitVector[rows];
        for (int i = rows; --i >= 0;) {
            rs[i] = new BitVector(size);
            for (int j = size; --j >= 0;) {
                rs[i].putQuick(j, f.apply(matrix[i][j]));

            }
        }
        return rs;
    }

    public static void compareMatrix(double[][] a, double[][] b) {
        System.out.println("Comparing matrix");
        for (int i = a.length; --i >= 0;) {
            for (int j = a[0].length; --j >= 0;) {
                if (Math.abs(a[i][j] - b[i][j]) > 0) {
                    System.out.println("[" + i + "," + j + "] a = " + a[i][j] + ", b = " + b[i][j]);
                }

            }

        }

    }

    /**
     * Compose 1D matrix (vector) from values of 2D matrix,
     * rows by rows
     * @param d2 m * n matrix
     * @return vector of length m*n with values from matrix
     */
    public static DoubleMatrix1D composeVector(DoubleMatrix2D d2) {
        int size = d2.size();
        DoubleMatrix1D d1 = d2.like1D(size);
        for (int i = size; --i >= 0;) {
            d1.assign(matrixToVector(d2.toArray()));
        }
        return d1;
    }

    /**
     * Compose 1D matrix (vector) from upper diagonal values of 2D square matrix,
     * rows by rows, top to bottom
     * @param d2 n * n matrix
     * @return vector of length (n-1)*n/2
     */
    public static DoubleMatrix1D upperDiagonalToVector(DoubleMatrix2D d2) {
        int size = d2.rows();
        if (size != d2.columns())
            throw new IllegalArgumentException("Square matrix is required !");
        int length = (size - 1) * size / 2;
        DoubleMatrix1D d1 = d2.like1D(length);
        /*for(int i=size; --i >= 0; ) {
            d1.assign(MatrixUtils.upperDiagonalToVector(d2.toArray()));
        }*/
        for (int i = size; --i >= 0;) {
            for (int j = size; --j > i;) {
                d1.setQuick(--length, d2.getQuick(i, j));
            }
        }
        return d1;
    }

    /**
     * Convert matrix to a vector by concatening matrix rows
     * @param matrix m * n matrix
     * @return vector of length m*n
     */
    public static double[] matrixToVector(double[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int size = rows * cols;
        double[] v = new double[size];
        for (int i = rows, k = size; --i >= 0;) {
            for (int j = cols; --j >= 0;) {
                v[--k] = matrix[i][j];
            }
        }
        return v;
    }


    /**
     * Compose a vector which values are taken from upper diagonal of a square matrix,
     * rows by rows are concatenated, from top to bottom.
     * @param matrix n*n matrix
     * @return vector of length (n-1) * n / 2
     */
    public static double[] upperDiagonalToVector(double[][] matrix) {
        int rows = matrix.length;
        if (matrix[0].length != rows)
            throw new IllegalArgumentException("Matrix size : " + rows + " * " + matrix[0].length + ", while a square matrix is required !");
        int size = (rows - 1) * rows / 2;
        double[] v = new double[size];
        for (int i = rows; --i >= 0;) {
            for (int j = rows; --j > i;) {
                v[--size] = matrix[i][j];
            }
        }
        return v;
    }

    /**
     * Calculate histogram for a collection of values.
     * Given range[] {start, end} is divided at given number of points
     * creating set of subranges.
     * The histogram is calculated as set of number of values that falls into each of
     * subrange
     * @param values
     * @param rangeStart
     * @param rangeEnd
     * @param division
     */
    public static int[] histogramSpread(DynamicBin1D values, double rangeStart, double rangeEnd, int division) {
        DoubleArrayList sorted = values.sortedElements();
        int[] histogram = new int[division];
        double subrangeLength = (rangeEnd - rangeStart) / division;
        double currentMax = rangeStart + subrangeLength;
        int size = sorted.size();
        int subrangeIndex = 0;
        for (int i = 0; i < size; i++) {
            if (sorted.getQuick(i) > currentMax) {
                currentMax += subrangeLength;
                if (subrangeIndex + 1 < division)
                    subrangeIndex++;
            }
            histogram[subrangeIndex]++;
        }
        return histogram;
    }

    /**
     * Divides the range [0..1] into number of parts and for each parts upper boundary
     * inverse quantile from element in bins (percent of elements <= part's boundary).
     * E.g. for a parts = 10 the range is split into 10 part at 0.1, 0.2, 0.3, .., 0.9, 1
     * and returned is an array of percent of element laying between ranges :
     * 0..0.1,0.1..0.2, ..., 0.9..1
     *
     * @param bins  collection of double values
     * @param parts number of parts in the range
     * @return array of inverse histogramPercentage for parts starting from smallest part's boundary
     */
    public static double[] histogramPercentage(DynamicBin1D bins, int parts) {
        double partLength = 1.0 / parts;
        double[] inverseQuantiles = new double[parts];
        double[] frequencyHistogram = new double[parts];
        double boundary = 0;
        for (int i = 0; i < parts; i++) {
            boundary += partLength;
            inverseQuantiles[i] = bins.quantileInverse(boundary);
        }
        frequencyHistogram[0] = inverseQuantiles[0];
        for (int i = 1; i < parts; i++) {
            frequencyHistogram[i] = inverseQuantiles[i] - inverseQuantiles[i - 1];
        }
        return frequencyHistogram;
    }


    public static boolean isEquals(DoubleMatrix2D a, DoubleMatrix2D b) {
        if ((a == null) || (b == null))
            return false;
        if ((a.rows() != b.rows()) || (a.columns() != b.columns()))
            return false;
        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.columns(); j++) {
                if (a.getQuick(i, j) != b.getQuick(i, j))
                    return false;
            }
        }
        return true;
    }

    /**
     * Check if two matrix has equal values
     * @param a
     * @param b
     */
    public static boolean isEquals(double[][] a, double[][] b) {
        if ((a == null) || (b == null))
            return false;
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++) {
            if ((a[i] == null) || (b[i] == null))
                return false;
            if (a[i].length != b[i].length)
                return false;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j])
                    return false;
            }
        }
        return true;
    }


}

