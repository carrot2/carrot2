/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.matrix;

import java.util.*;

import cern.colt.matrix.*;

/**
 * Provides a set of useful Colt DoubleMatrix2D shorthands and utility methods.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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
     * Computers sparesness of matrix <code>A</code> as a fraction of non-zero
     * elements to the total number of elements.
     * 
     * @param A
     * @return sparseness of <code>A</code>, which is a value between 0.0
     *         (all elements are zero) and 1.0 (all elements are non-zero)
     */
    public static double computeSparseness(DoubleMatrix2D A)
    {
        int count = 0;

        for (int r = 0; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                if (A.getQuick(r, c) != 0)
                {
                    count++;
                }
            }
        }

        return count / (double) (A.rows() * A.columns());
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
     * Finds the first minimum element in each column of matrix A. When
     * calculating minimum values for each column this version should perform
     * better than scannning each column separately.
     * 
     * @param A
     * @param indices an array of <code>A.columns()</code> integers in which
     *            indices of the first minimum element will be stored. If this
     *            parameter is <code>null<code> a new array will be allocated.
     * @param minValues an array of <code>A.columns()</code> doubles in which
     *  		  values of each column's minimum elements will be stored. If 
     * 			  this parameter is <code>null<code> a new array will be 
     *            allocated. 
     * 
     * @return for each column of A the index of the minimum element
     */
    public static int [] minInColumns(DoubleMatrix2D A, int [] indices,
        double [] minValues)
    {
        if (indices == null)
        {
            indices = new int [A.columns()];
        }

        if (A.columns() == 0 || A.rows() == 0)
        {
            return indices;
        }

        if (minValues == null)
        {
            minValues = new double [A.columns()];
        }
        
        for (int c = 0; c < A.columns(); c++)
        {
            minValues[c] = A.getQuick(0, c);
        }
        Arrays.fill(indices, 0);

        for (int r = 1; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                if (A.getQuick(r, c) < minValues[c])
                {
                    minValues[c] = A.getQuick(r, c);
                    indices[c] = r;
                }
            }
        }

        return indices;
    }

    /**
     * Finds the first maximum element in each column of matrix A. When
     * calculating maximum values for each column this version should perform
     * better than scannning each column separately.
     * 
     * @param A
     * @param indices an array of <code>A.columns()</code> integers in which
     *            indices of the first maximum element will be stored. If this
     *            parameter is <code>null<code> a new array will be allocated.
     * @param maxValues an array of <code>A.columns()</code> doubles in which
     *  		  values of each column's maximum elements will be stored. If 
     * 			  this parameter is <code>null<code> a new array will be 
     *            allocated. 
     * 
     * @return for each column of A the index of the maximum element
     */
    public static int [] maxInColumns(DoubleMatrix2D A, int [] indices,
        double [] maxValues)
    {
        if (indices == null)
        {
            indices = new int [A.columns()];
        }

        if (A.columns() == 0 || A.rows() == 0)
        {
            return indices;
        }

        if (maxValues == null)
        {
            maxValues = new double [A.columns()];
        }

        for (int c = 0; c < A.columns(); c++)
        {
            maxValues[c] = A.getQuick(0, c);
        }
        Arrays.fill(indices, 0);

        for (int r = 1; r < A.rows(); r++)
        {
            for (int c = 0; c < A.columns(); c++)
            {
                if (A.getQuick(r, c) > maxValues[c])
                {
                    maxValues[c] = A.getQuick(r, c);
                    indices[c] = r;
                }
            }
        }

        return indices;
    }

    /**
     * Finds the index of the first maximum element in given row of
     * <code>A</code>
     * 
     * @param A the matrix to search
     * @param row the row to search
     * @return index of the first maxiumum element or -1 if the input matrix is
     *         <code>null</code> or has zero size.
     */
    public static int maxInRow(DoubleMatrix2D A, int row)
    {
        if (A == null && A.rows() == 0 || A.columns() == 0 || row >= A.rows())
        {
            return -1;
        }

        int index = 0;
        double max = A.getQuick(row, index);
        for (int c = 1; c < A.columns(); c++)
        {
            if (max < A.getQuick(row, c))
            {
                max = A.getQuick(row, c);
                index = c;
            }
        }

        return index;
    }
}