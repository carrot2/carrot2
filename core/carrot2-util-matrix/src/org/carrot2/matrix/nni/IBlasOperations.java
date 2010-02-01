package org.carrot2.matrix.nni;

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;

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
