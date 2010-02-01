package org.carrot2.matrix.nni;

import nni.BLAS;
import nni.LAPACK;

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;

/**
 * 
 */
public final class LapackBridge implements NativeOps
{
    public LapackBridge()
    {
        nni.BLAS.init();
        nni.LAPACK.init();
    }

    public double [] computeEigenvaluesSymmetricalNNI(NNIDenseDoubleMatrix2D A)
    {
        // Some parts of the code borrowed from NNI
        // Find and allocate work space
        double [] work = new double [1];
        int [] info = new int [1];
        int [] isuppz = new int [2 * Math.max(1, A.rows())];
        int [] iwork = new int [1];
        double abstol = LAPACK.lamch("Safe minimum".toCharArray());

        LAPACK.syevr(new char []
        {
            'N'
        }, new char []
        {
            'A'
        }, new char []
        {
            'L'
        }, new int []
        {
            A.rows()
        }, new double [0], new int []
        {
            Math.max(1, A.rows())
        }, new double []
        {
            0
        }, new double []
        {
            0
        }, new int []
        {
            0
        }, new int []
        {
            0
        }, new double []
        {
            abstol
        }, new int [1], new double [0], new double [0], new int []
        {
            Math.max(1, A.rows())
        }, isuppz, work, new int []
        {
            -1
        }, iwork, new int []
        {
            -1
        }, info);

        // Allocate workspace
        int lwork = 0, liwork = 0;
        if (info[0] != 0)
        {
            lwork = 26 * A.rows();
            liwork = 10 * A.rows();
        }
        else
        {
            lwork = (int) work[0];
            liwork = iwork[0];
        }
        lwork = Math.max(1, lwork);
        liwork = Math.max(1, liwork);
        work = new double [lwork];
        iwork = new int [liwork];

        // Calculate the eigenvalues
        double [] wr = new double [A.rows()];

        // Copy the data array of the A matrix (LAPACK will overwrite the
        // input data)
        final double [] data = A.getData();
        double [] dataA = new double [data.length];
        System.arraycopy(data, 0, dataA, 0, dataA.length);

        LAPACK.syevr(new char []
        {
            'N'
        }, new char []
        {
            'A'
        }, new char []
        {
            'L'
        }, new int []
        {
            A.rows()
        }, dataA, new int []
        {
            Math.max(1, A.rows())
        }, new double []
        {
            0
        }, new double []
        {
            0
        }, new int []
        {
            0
        }, new int []
        {
            0
        }, new double []
        {
            abstol
        }, new int [1], wr, new double [0], new int []
        {
            Math.max(1, A.rows())
        }, isuppz, work, new int []
        {
            work.length
        }, iwork, new int []
        {
            iwork.length
        }, info);

        return wr;
    }

    /**
     * Computes eigenvalues matrix <code>A</code>, requires that NNI is available.
     */
    public double [] computeEigenvaluesNNI(NNIDenseDoubleMatrix2D A)
    {
        // Some parts of the code borrowed from NNI
        double [] work;

        // Find and allocate work space
        work = new double [1];
        int [] info = new int [1];

        LAPACK.geev(new char []
        {
            'N'
        }, new char []
        {
            'N'
        }, new int []
        {
            A.rows()
        }, new double [0], new int []
        {
            Math.max(1, A.rows())
        }, new double [0], new double [0], new double [0], new int []
        {
            Math.max(1, A.rows())
        }, new double [0], new int []
        {
            Math.max(1, A.rows())
        }, work, new int []
        {
            -1
        }, info);

        int lwork = 0;
        if (info[0] != 0)
        {
            lwork = 3 * A.rows();
        }
        else
        {
            lwork = (int) work[0];
        }
        lwork = Math.max(1, lwork);
        work = new double [lwork];

        // Calculate the eigenvalues
        double [] wr = new double [A.rows()];
        double [] wi = new double [A.rows()];

        // Copy the data array of the A matrix (LAPACK will overwrite the
        // input data)
        double [] data = A.getData();
        double [] dataA = new double [data.length];
        System.arraycopy(data, 0, dataA, 0, dataA.length);

        LAPACK.geev(new char []
        {
            'N'
        }, new char []
        {
            'N'
        }, new int []
        {
            A.rows()
        }, dataA, new int []
        {
            Math.max(1, A.rows())
        }, wr, wi, new double [0], new int []
        {
            Math.max(1, A.rows())
        }, new double [0], new int []
        {
            Math.max(1, A.rows())
        }, work, new int []
        {
            work.length
        }, info);

        return wr;
    }

    public void gemm(NNIDenseDoubleMatrix2D A, NNIDenseDoubleMatrix2D B,
        NNIDenseDoubleMatrix2D C, boolean transposeA, boolean transposeB, int columnsA,
        double alpha, int columns, double beta)
    {
        // Get the matrices data. It is in row-major format.
        final double [] dataA = A.getData();
        final double [] dataB = B.getData();
        final double [] dataC = C.getData();

        // Multiply
        BLAS.gemm(BLAS.RowMajor, 
            transposeA ? BLAS.Trans : BLAS.NoTrans,
            transposeB ? BLAS.Trans : BLAS.NoTrans, 
            C.rows(), C.columns(), columnsA, alpha, 
            dataA, Math.max(1, columns), 
            dataB, Math.max(1, B.columns()), 
            beta,
            dataC, Math.max(1, C.columns()));
    }
    
    public void gesdd(int n, int m, double [] dataA, int [] info,
        double [] S, double [] vd, double [] ud, double [] work, int [] iwork)
    {
        LAPACK.gesdd(
            new char [] {'S'}, 
            new int [] {m}, 
            new int [] {n}, 
            dataA, 
            new int [] { Math.max(1, m) }, 
            S, 
            vd, 
            new int [] { Math.max(1, m) }, 
            ud, 
            new int [] { Math.max(1, n) }, 
            work, 
            new int [] { work.length }, 
            iwork, 
            info);
    }    
}
