
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import nni.LAPACK;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.linalg.EigenvalueDecomposition;

/**
 * Calculates eigenvalues for given matrix. Uses Native Numerical Interface (NNI) calls to
 * speed up calculations wherever possible.
 */
public class EigenvalueCalculator
{
    /**
     * Computes eigenvalues a symmetrical matrix <code>A</code>, uses NNI if possible.
     */
    public static double [] computeEigenvaluesSymmetrical(DoubleMatrix2D A)
    {
        // Need native LAPACK, dense matrices and no views to operate
        // Default to Colt's implementation otherwise
        if (!NNIInterface.isNativeLapackAvailable()
            || (!(A instanceof NNIDenseDoubleMatrix2D))
            || ((NNIDenseDoubleMatrix2D) A).isView())
        {
            return new EigenvalueDecomposition(A).getRealEigenvalues().toArray();
        }
        else
        {
            return computeEigenvaluesSymmetricalNNI((NNIDenseDoubleMatrix2D) A);
        }
    }

    static double [] computeEigenvaluesSymmetricalNNI(NNIDenseDoubleMatrix2D A)
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
    public static double [] computeEigenvaluesNNI(NNIDenseDoubleMatrix2D A)
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
}
