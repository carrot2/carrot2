
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

import nni.BLAS;

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;

/**
 * JNI bridge to BLAS library.
 */
@SuppressWarnings("deprecation")
public final class BlasImpl implements IBlasOperations
{
    private static boolean initialized;

    private static synchronized void lazyInit()
    {
        if (initialized) return;

        nni.BLAS.init();
        initialized = true;
    }

    public BlasImpl()
    {
        lazyInit();
    }

    // TODO: this should be pulled up from here, perhaps. BLAS constants
    // to the interface and the implementation a simple proxy.
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
}
