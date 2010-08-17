
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

import static org.junit.Assume.assumeTrue;

import java.util.Arrays;

import org.junit.Test;

import org.apache.mahout.math.matrix.*;
import org.apache.mahout.math.matrix.linalg.EigenvalueDecomposition;

/**
 * Test cases for {@link EigenvalueCalculator}.
 */
@SuppressWarnings("deprecation")
public class EigenvalueCalculatorTest
{
    /** Default delta for comparisons */
    private static final double DELTA = 1e-6;

    /** The test input matrix */
    private NNIDenseDoubleMatrix2D A = (NNIDenseDoubleMatrix2D) NNIDoubleFactory2D.nni
        .make(new double [] []
        {
            {
                1.00, 7.00, 0.00, 1.00, 0.00
            },
            {
                4.00, 2.00, 0.00, 0.00, 0.00
            },
            {
                0.00, 2.00, 3.00, 7.00, 9.00
            },
            {
                1.00, 5.00, 4.00, 4.00, 3.00
            },
            {
                0.00, 0.00, 6.00, 3.00, 5.00
            }
        });

    @Test
    public void testSymmetrical()
    {
        DoubleMatrix2D Asym = A.zMult(A, null, 1, 0, true, false);
        double [] expectedEigenvalues = new EigenvalueDecomposition(Asym)
            .getRealEigenvalues().toArray();
        Arrays.sort(expectedEigenvalues);

        double [] eigenvalues = EigenvalueCalculator.computeEigenvaluesSymmetrical(Asym);
        Arrays.sort(eigenvalues);

        org.junit.Assert.assertArrayEquals(expectedEigenvalues, eigenvalues, DELTA);
    }

    @Test
    public void testAsymmetrical()
    {
        assumeTrue(NNIInterface.isNativeLapackAvailable());

        double [] eigenvalues = NNIInterface.getLapack().computeEigenvaluesNNI(A);
        Arrays.sort(eigenvalues);

        double [] expectedEigenvalues = new EigenvalueDecomposition(A)
            .getRealEigenvalues().toArray();
        Arrays.sort(expectedEigenvalues);

        org.junit.Assert.assertArrayEquals(expectedEigenvalues, eigenvalues, DELTA);
    }
}
