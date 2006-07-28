
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

package cern.colt.matrix.impl;

import nni.BLAS;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * A very crude native implementation of Colt's @link cern.colt.matrix.DoubleMatrix2D 
 * based on the Native Numerical Interface (NNI). For the time being, the only method
 * that uses the native routines is the Level 3 zMult(). The other methods use the 
 * implementations provided in DenseDoubleMatrix2D.
 * 
 * TODO: implement like() methods
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class NNIDenseDoubleMatrix2D extends DenseDoubleMatrix2D
{
    /**
     * @param values
     */
    public NNIDenseDoubleMatrix2D(double [][] values)
    {
        super(values);
    }

    /**
     * @param rows
     * @param columns
     */
    public NNIDenseDoubleMatrix2D(int rows, int columns)
    {
        super(rows, columns);
    }

    /**
     * @param rows
     * @param columns
     * @param elements
     * @param rowZero
     * @param columnZero
     * @param rowStride
     * @param columnStride
     */
    public NNIDenseDoubleMatrix2D(int rows, int columns, double [] elements,
        int rowZero, int columnZero, int rowStride, int columnStride)
    {
        super(rows, columns, elements, rowZero, columnZero, rowStride,
            columnStride);
    }

    /*
     * (non-Javadoc)
     * 
     * @see cern.colt.matrix.DoubleMatrix2D#zMult(cern.colt.matrix.DoubleMatrix1D,
     *      cern.colt.matrix.DoubleMatrix1D, double, double, boolean)
     */
    public DoubleMatrix1D zMult(DoubleMatrix1D y, DoubleMatrix1D z,
        double alpha, double beta, boolean transposeA)
    {
        return super.zMult(y, z, alpha, beta, transposeA);

    }

    /**
     * A native implementation of the @link cern.colt.matrix.DoubleMatrix2D#zMult(cern.colt.matrix.DoubleMatrix2D,
     * cern.colt.matrix.DoubleMatrix2D, double, double, boolean, boolean) method. 
     * NOTE: the method will use native calls only when:
     * 
     * <ul>
     *   <li>all input matrices are @link DenseDoubleMatrix2D or its subclasses (e.g. @link NNIDenseDoubleMatrix2D)
     *   <li>none of the input matrices is a view
     *   <li>the dynamic libraries required by the NNI are available
     * </ul>
     */
    public DoubleMatrix2D zMult(DoubleMatrix2D B, DoubleMatrix2D C,
        double alpha, double beta, boolean transposeA, boolean transposeB)
    {
        // A workaround for a bug in DenseDoubleMatrix2D
        // If B is a SelectedDenseDoubleMatrix the implementation of this method
        // throws a ClassCastException. The workaround is to swap and transpose
        // the arguments and then transpose the result
        if (B instanceof SelectedDenseDoubleMatrix2D)
        {
            return B.zMult(this, C, alpha, beta, !transposeB, !transposeA)
                .viewDice();
        }

        // Check the sizes
        int rowsB = (transposeB ? B.columns : B.rows);
        int columnsB = (transposeB ? B.rows : B.columns);
        int rowsA = (transposeA ? columns : rows);
        int columnsA = (transposeA ? rows : columns);

        if (C == null)
        {
            C = new NNIDenseDoubleMatrix2D(rowsA, columnsB);
        }

        if (this == C || B == C)
        {
            throw new IllegalArgumentException("Matrices must not be identical");
        }

        int rowsC = C.rows;
        int columnsC = C.columns;

        if (rowsB != columnsA)
        {
            throw new IllegalArgumentException(
                "Matrix2D inner dimensions must agree:" + toStringShort()
                    + ", " + B.toStringShort());
        }

        if (rowsC != rowsA || columnsC != columnsB)
        {
            throw new IllegalArgumentException("Incompatibile result matrix: "
                + toStringShort() + ", " + B.toStringShort() + ", "
                + C.toStringShort());
        }

        // Need native BLAS, dense matrices and no views to operate
        // Default to Colt's implementation otherwise
        if (!NNIInterface.isNativeBlasAvailable()
            || (!(B instanceof DenseDoubleMatrix2D))
            || (!(C instanceof DenseDoubleMatrix2D)) || !isNoView
            || !B.isNoView || !C.isNoView)
        {
            return super.zMult(B, C, alpha, beta, transposeA, transposeB);
        }

        // Get the matrices data. It is in row-major format.
        final double [] dataA = this.elements;
        final double [] dataB = ((DenseDoubleMatrix2D) B).elements;
        final double [] dataC = ((DenseDoubleMatrix2D) C).elements;

        // Multiply
        BLAS.gemm(BLAS.RowMajor, transposeA ? BLAS.Trans : BLAS.NoTrans,
            transposeB ? BLAS.Trans : BLAS.NoTrans, C.rows(), C.columns(),
            columnsA, alpha, dataA, Math.max(1, columns), dataB, Math.max(1, B
                .columns()), beta, dataC, Math.max(1, C.columns()));

        return C;
    }

    /**
     * Hacky, hacky, hacky! Unfortunately, the underlying double array is
     * protected and there is no other way of getting hold of it.
     * 
     * @param A
     */
    public static double [] getDoubleData(DenseDoubleMatrix2D A)
    {
        return A.elements;
    }

    /**
     * Hacky, hacky, hacky! Unfortunately, the isView() of the AbstractMatrix is
     * protected.
     * 
     * @param A
     */
    public static boolean isView(DenseDoubleMatrix2D A)
    {
        return A.isView();
    }

    /**
     * Transposes in place matrix A. Note: the algorithm is simple but very
     * inefficient with respect to cache utilisation
     * 
     * @param A
     */
    public static void deepTranspose(DenseDoubleMatrix2D A)
    {
        double [] data = A.elements;

        int from = 2;
        int to = 0;
        double store = data[from];
        double temp;
        do
        {
            to = (from % A.columns) + (from / A.rows);
            temp = data[to];
            data[to] = store;
            store = temp;

            from = to;
        }
        while (from != 2);

        int tmp;
        tmp = A.rows;
        A.rows = A.columns;
        A.columns = tmp;
        tmp = A.rowStride;
        A.rowStride = A.columnStride;
        A.columnStride = tmp;
        tmp = A.rowZero;
        A.rowZero = A.columnZero;
        A.columnZero = tmp;
    }
}