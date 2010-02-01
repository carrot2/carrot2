package org.carrot2.matrix.nni;

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;

public interface NativeOps
{
    public double [] computeEigenvaluesSymmetricalNNI(NNIDenseDoubleMatrix2D A);
    public double [] computeEigenvaluesNNI(NNIDenseDoubleMatrix2D A);

    public void gemm(NNIDenseDoubleMatrix2D A, NNIDenseDoubleMatrix2D B, NNIDenseDoubleMatrix2D C, 
        boolean transposeA, boolean transposeB, 
        int columnsA, double alpha, int columns, double beta);
    
    public void gesdd(int n, int m, double [] dataA, int [] info,
        double [] S, double [] vd, double [] ud, double [] work, int [] iwork);
}
