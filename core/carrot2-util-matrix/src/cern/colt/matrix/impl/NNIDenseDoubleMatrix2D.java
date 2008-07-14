package cern.colt.matrix.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import nni.BLAS;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * A very crude native implementation of Colt's @link cern.colt.matrix.DoubleMatrix2D
 * based on the Native Numerical Interface (NNI). For the time being, the only method that
 * uses the native routines is the Level 3 zMult(). The other methods use the
 * implementations provided in DenseDoubleMatrix2D.
 */
public class NNIDenseDoubleMatrix2D extends DenseDoubleMatrix2D
{
    private static final long serialVersionUID = 1L;

    public NNIDenseDoubleMatrix2D(double [][] values)
    {
        super(values);
    }

    public NNIDenseDoubleMatrix2D(int rows, int columns)
    {
        super(rows, columns);
    }

    public NNIDenseDoubleMatrix2D(int rows, int columns, double [] elements, int rowZero,
        int columnZero, int rowStride, int columnStride)
    {
        super(rows, columns, elements, rowZero, columnZero, rowStride, columnStride);
    }

    public DoubleMatrix1D zMult(DoubleMatrix1D y, DoubleMatrix1D z, double alpha,
        double beta, boolean transposeA)
    {
        return super.zMult(y, z, alpha, beta, transposeA);

    }

    /**
     * A native implementation of Colt's original multiplication method method.
     * <p>
     * NOTE: this method will use native calls only when:
     * <ul>
     * <li>all input matrices are @link DenseDoubleMatrix2D or its subclasses (e.g. @link
     * NNIDenseDoubleMatrix2D)
     * <li>none of the input matrices is a view
     * <li>the dynamic libraries required by the NNI are available
     * </ul>
     */
    public DoubleMatrix2D zMult(DoubleMatrix2D B, DoubleMatrix2D C, double alpha,
        double beta, boolean transposeA, boolean transposeB)
    {
        // A workaround for a bug in DenseDoubleMatrix2D.
        // If B is a SelectedDenseDoubleMatrix the implementation of this method
        // throws a ClassCastException. The workaround is to swap and transpose
        // the arguments and then transpose the result. As SelectedDenseDoubleMatrix2D is
        // package-private, if it was loaded with a different class loader than
        // the one used for this class it would give a VerificationError if we referred
        // to it directly here. Hence the hacky string comparison here.
        // 
        if (B.getClass().getName().endsWith("SelectedDenseDoubleMatrix2D"))
        {
            return B.zMult(this, C, alpha, beta, !transposeB, !transposeA).viewDice();
        }

        // Check the sizes
        int rowsB = (transposeB ? B.columns() : B.rows());
        int columnsB = (transposeB ? B.rows() : B.columns());
        int rowsA = (transposeA ? columns() : rows());
        int columnsA = (transposeA ? rows() : columns());

        if (C == null)
        {
            C = new NNIDenseDoubleMatrix2D(rowsA, columnsB);
        }

        if (this == C || B == C)
        {
            throw new IllegalArgumentException("Matrices must not be identical");
        }

        final int rowsC = C.rows();
        final int columnsC = C.columns();

        if (rowsB != columnsA)
        {
            throw new IllegalArgumentException("Matrix2D inner dimensions must agree:"
                + toStringShort() + ", " + B.toStringShort());
        }

        if (rowsC != rowsA || columnsC != columnsB)
        {
            throw new IllegalArgumentException("Incompatibile result matrix: "
                + toStringShort() + ", " + B.toStringShort() + ", " + C.toStringShort());
        }

        // Need native BLAS, dense matrices and no views to operate
        // Default to Colt's implementation otherwise
        if (!NNIInterface.isNativeBlasAvailable()
            || (!(B instanceof DenseDoubleMatrix2D))
            || (!(C instanceof DenseDoubleMatrix2D)) || !isNoView
            || isView((DenseDoubleMatrix2D) B) || isView((DenseDoubleMatrix2D) C))
        {
            return super.zMult(B, C, alpha, beta, transposeA, transposeB);
        }

        // Get the matrices data. It is in row-major format.
        final double [] dataA = this.elements;
        final double [] dataB = getDoubleData((DenseDoubleMatrix2D) B);
        final double [] dataC = getDoubleData((DenseDoubleMatrix2D) C);

        // Multiply
        BLAS.gemm(BLAS.RowMajor, transposeA ? BLAS.Trans : BLAS.NoTrans,
            transposeB ? BLAS.Trans : BLAS.NoTrans, C.rows(), C.columns(), columnsA,
            alpha, dataA, Math.max(1, columns), dataB, Math.max(1, B.columns()), beta,
            dataC, Math.max(1, C.columns()));

        return C;
    }

    /**
     * Exposes the underlying data array of {@link DenseDoubleMatrix2D}.
     */
    public static double [] getDoubleData(DenseDoubleMatrix2D A)
    {
        try
        {
            final Field elementsField = DenseDoubleMatrix2D.class
                .getDeclaredField("elements");
            elementsField.setAccessible(true);
            return (double []) elementsField.get(A);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the provided {@link DenseDoubleMatrix2D} is a view.
     */
    public static boolean isView(DenseDoubleMatrix2D A)
    {
        try
        {
            final Method isViewMethod = AbstractMatrix.class.getDeclaredMethod("isView");
            isViewMethod.setAccessible(true);
            return (Boolean) isViewMethod.invoke(A);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Transposes in place matrix A. Note that this algorithm is simple but very
     * inefficient with respect to cache utilization.
     * 
     * @param A matrix to be transposed in place
     */
    public static void deepTranspose(DenseDoubleMatrix2D A)
    {
        double [] data = getDoubleData(A);

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