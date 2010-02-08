
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
 * Operations used from native LAPACK implementations.
 */
public interface ILapackOperations
{
    public double [] computeEigenvaluesSymmetricalNNI(NNIDenseDoubleMatrix2D A);

    public double [] computeEigenvaluesNNI(NNIDenseDoubleMatrix2D A);

    public void gesdd(char [] arg0, int [] arg1, int [] arg2, double [] arg3,
        int [] arg4, double [] arg5, double [] arg6, int [] arg7, double [] arg8,
        int [] arg9, double [] arg10, int [] arg11, int [] arg12, int [] arg13);
}
