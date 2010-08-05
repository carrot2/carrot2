
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

package org.carrot2.matrix.nni;

import java.util.Arrays;

import nni.LAPACK;

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;

/**
 * JNI bridge to BLAS library.
 */
public final class LapackImpl implements ILapackOperations
{
    private static boolean initialized;

    private static synchronized void lazyInit()
    {
        if (initialized) return;

        nni.LAPACK.init();
        initialized = true;
    }

    public LapackImpl()
    {
        lazyInit();
    }

    @SuppressWarnings("deprecation")
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
        double [] dataA = Arrays.copyOf(data, data.length);

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
    @SuppressWarnings("deprecation")
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
        double [] dataA = Arrays.copyOf(data, data.length);

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

    public void gesdd(char [] arg0, int [] arg1, int [] arg2, double [] arg3,
        int [] arg4, double [] arg5, double [] arg6, int [] arg7, double [] arg8,
        int [] arg9, double [] arg10, int [] arg11, int [] arg12, int [] arg13)
    {
        LAPACK.gesdd(
            arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7,
            arg8, arg9, arg10, arg11, arg12, arg13);
    }
}
