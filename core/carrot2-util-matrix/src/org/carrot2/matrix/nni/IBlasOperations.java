
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

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;

/**
 * Operations used from native BLAS implementations.
 */
public interface IBlasOperations
{
    public void gemm(
        NNIDenseDoubleMatrix2D A, 
        NNIDenseDoubleMatrix2D B,
        NNIDenseDoubleMatrix2D C, 
        boolean transposeA, 
        boolean transposeB, 
        int columnsA,
        double alpha, 
        int columns, double beta);
}
