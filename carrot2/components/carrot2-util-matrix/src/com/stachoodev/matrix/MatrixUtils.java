/*
 * MatrixUtils.java Created on 2004-05-11
 */
package com.stachoodev.matrix;

import java.util.*;

import cern.colt.matrix.*;

/**
 * Provides a set of useful Colt DoubleMatrix2D shorthands and utility methods.
 * 
 * @author stachoo
 */
public class MatrixUtils
{
    /**
     * Normalises column vectors of matrix <code>A</code> so that their L2
     * norm (Euclidean distance) is equal to 1.0.
     * 
     * @param A
     * @param work a temporary array of <code>A.columns()</code> doubles that
     *            will be overwritten with column's original L2 norms. Supply a
     *            non-null pointer to avoid continuous allocation/freeing of
     *            memory when doing calculations in a loop. If this parameter is
     *            <code>null</code>, a new array will be allocated every time
     *            this method is called.
     * @return A with length-normalised columns (for convenience only)
     */
    public static DoubleMatrix2D normaliseColumnL2(DoubleMatrix2D A,
        double [] work)
    {
        // Note: This straightforward implementation may cause deterioration of
        // performance in case of sparse matricess
        for (int c = 0; c < A.columns(); c++)
        {
            double length = 0;
            for (int r = 0; r < A.rows(); r++)
            {
                length += A.getQuick(r, c) * A.getQuick(r, c);
            }

            length = Math.sqrt(length);

            for (int r = 0; r < A.rows(); r++)
            {
                A.setQuick(r, c, A.getQuick(r, c) / length);
            }
        }

        // Colt's dense matrices are stored in a row-major format, so the
        // processor's cache will be better used when the rows counter is in the
        // outer loop. To do that we need a temporaty double vector
        if (work == null || work.length != A.columns())
        {
            work = new double [A.columns()];
        }
        else
        {
            Arrays.fill(work, 0);
        }

        // Calculate the L1 norm for each column
        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                work[c] += A.getQuick(r, c) * A.getQuick(r, c);
            }
        }

        // Take the square root
        for (int c = 0; c < A.columns(); c++)
        {
            work[c] = Math.sqrt(work[c]);
        }

        // Normalise
        for (int r = A.rows() - 1; r >= 0; r--)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                A.setQuick(r, c, A.getQuick(r, c) / work[c]);
            }
        }

        return A;
    }

    /**
     * Normalises column vectors of matrix <code>A</code> so that their L1
     * norm is equal to 1.0.
     * 
     * @param A
     * @param work a temporary array of <code>A.columns()</code> doubles that
     *            will be overwritten with column's original L1 norms. Supply a
     *            non-null pointer to avoid continuous allocation/freeing of
     *            memory when doing calculations in a loop. If this parameter is
     *            <code>null</code>, a new array will be allocated every time
     *            this method is called.
     * @return A with L1-normalised columns (for convenience only)
     */
    public static DoubleMatrix2D normaliseColumnL1(DoubleMatrix2D A,
        double [] work)
    {
        // Colt's dense matrices are stored in a row-major format, so the
        // processor's cache will be better used when the rows counter is in the
        // outer loop. To do that we need a temporaty double vector
        if (work == null || work.length != A.columns())
        {
            work = new double [A.columns()];
        }
        else
        {
            Arrays.fill(work, 0);
        }

        // Calculate the L1 norm for each column
        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                work[c] += A.getQuick(r, c);
            }
        }

        // Normalise
        for (int r = A.rows() - 1; r >= 0; r--)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                A.setQuick(r, c, A.getQuick(r, c) / work[c]);
            }
        }

        return A;
    }

    /**
     * Computes the orthogonality of matrix A. Columns of matrix A must be
     * length-normalised. The orthogonality is computed as a sum of k*(k-1)/2
     * inner products of A's column vectors, k being the number of columns of A,
     * and then normalised to the 0.0 - 1.0 range.
     * 
     * @param A
     * @return orthogonality of matrix A. 0.0 denotes a perfect orthogonality
     *         between every pair of A's column. 1.0 indicates that all columns
     *         of A are parallel.
     */
    public static double computeOrthogonality(DoubleMatrix2D A)
    {
        double orthogonality = 0;

        // Compute pairwise inner products
        DoubleMatrix2D cosines = A.zMult(A, null, 1, 0, true, false);

        for (int r = 0; r < cosines.rows(); r++)
        {
            for (int c = r + 1; c < cosines.columns(); c++)
            {
                orthogonality += cosines.getQuick(r, c);
            }
        }

        return orthogonality / ((cosines.rows() - 1) * cosines.rows() / 2);
    }

    /**
     * Compares two matrices. Matrices are considered equal if they are of the
     * same size, and the absolute difference between their corresponding
     * elements is not greater than <code>delta</code>.
     * 
     * @param A
     * @param B
     * @param delta
     * @return true when matrices <code>A</code> and <code>B</code> are
     *         equal.
     */
    public static boolean equals(DoubleMatrix2D A, DoubleMatrix2D B,
        double delta)
    {
        if (A.columns() != B.columns() || A.rows() != B.rows())
        {
            return false;
        }

        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                if (Math.abs(A.getQuick(r, c) - B.getQuick(r, c)) > delta)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Calculates maximum elements in each column of matrix A. When calculating
     * maximum values for each column this version should perform better than
     * scannning each column separately.
     * 
     * @param A
     * @return for each column of A the index of the maximum element
     */
    public static int [] maxInColumn(DoubleMatrix2D A)
    {
        if (A.columns() == 0 || A.rows() == 0)
        {
            return new int [0];
        }
        
        int [] indices = new int [A.columns()];
        double [] max = new double[A.columns()];
        
        for (int c = 0; c < A.columns(); c++)
        {
            max[c] = A.getQuick(0, c);
        }
        
        for (int r = 1; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                if (A.getQuick(r, c) > max[c])
                {
                    max[c] = A.getQuick(r, c);
                    indices[c] = r;
                }
            }
        }
        
        return indices;
    }
}