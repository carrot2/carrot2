package org.carrot2.matrix.nni;

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;

public interface ILapackOperations
{
    public double [] computeEigenvaluesSymmetricalNNI(NNIDenseDoubleMatrix2D A);

    public double [] computeEigenvaluesNNI(NNIDenseDoubleMatrix2D A);

    public void gesdd(char [] arg0, int [] arg1, int [] arg2, double [] arg3,
        int [] arg4, double [] arg5, double [] arg6, int [] arg7, double [] arg8,
        int [] arg9, double [] arg10, int [] arg11, int [] arg12, int [] arg13);
}
