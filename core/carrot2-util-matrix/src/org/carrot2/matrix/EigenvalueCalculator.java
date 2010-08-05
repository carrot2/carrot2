
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

package org.carrot2.matrix;

import org.apache.mahout.math.matrix.*;
import org.apache.mahout.math.matrix.linalg.*;

/**
 * Calculates eigenvalues for given matrix. Uses Native Numerical Interface (NNI) calls to
 * speed up calculations wherever possible.
 */
@SuppressWarnings("deprecation")
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
            return NNIInterface.getLapack().computeEigenvaluesSymmetricalNNI(
                (NNIDenseDoubleMatrix2D) A);
        }
    }
}
