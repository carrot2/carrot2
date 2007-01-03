
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import java.util.Arrays;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.NNIDoubleFactory2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.NNIInterface;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

/**
 * @author Stanislaw Osinski
 * @version $Revision: 838 $
 */
public class EigenvalueCalculatorTest extends TestCase
{
    /** The test input matrix */
    private DoubleMatrix2D A = NNIDoubleFactory2D.nni.make(new double [] []
    {
    { 1.00, 7.00, 0.00, 1.00, 0.00 },
    { 4.00, 2.00, 0.00, 0.00, 0.00 },
    { 0.00, 2.00, 3.00, 7.00, 9.00 },
    { 1.00, 5.00, 4.00, 4.00, 3.00 },
    { 0.00, 0.00, 6.00, 3.00, 5.00 } });

    /**
     *  
     */
    public void testSymmetrical()
    {
        DoubleMatrix2D Asym = A.zMult(A, null, 1, 0, true, false);
        double [] expectedEigenvalues = new EigenvalueDecomposition(Asym)
            .getRealEigenvalues().toArray();
        Arrays.sort(expectedEigenvalues);

        double [] eigenvalues = EigenvalueCalculator
            .computeEigenvaluesSymmetrical((DenseDoubleMatrix2D) Asym);
        Arrays.sort(eigenvalues);

        ArrayAssert.assertEquals("Equal real eigenvalues", expectedEigenvalues,
            eigenvalues, 1e-6);
    }

    /**
     *  
     */
    public void testAsymmetrical()
    {
	    if (NNIInterface.isNativeLapackAvailable()==false) {
		    // IF NNI is not available, skip this test.
		    return;
	    }

        double [] eigenvalues = EigenvalueCalculator
            .computeEigenvaluesNNI((DenseDoubleMatrix2D) A);
        Arrays.sort(eigenvalues);

        double [] expectedEigenvalues = new EigenvalueDecomposition(A)
            .getRealEigenvalues().toArray();
        Arrays.sort(expectedEigenvalues);

        ArrayAssert.assertEquals("Equal real eigenvalues", expectedEigenvalues,
            eigenvalues, 1e-6);
    }
}