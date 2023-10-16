/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.math.matrix;

import org.carrot2.math.mahout.matrix.*;

/**
 * Performs matrix factorization using the Non-negative Matrix Factorization algorithm with
 * minimization of Euclidean Distance between A and UV' and multiplicative updating.
 */
public class NonnegativeMatrixFactorizationEDFactory extends IterativeMatrixFactorizationFactory {
  public MatrixFactorization factorize(DoubleMatrix2D A) {
    NonnegativeMatrixFactorizationED factorization = new NonnegativeMatrixFactorizationED(A);
    factorization.setK(k);
    factorization.setMaxIterations(maxIterations);
    factorization.setStopThreshold(stopThreshold);
    factorization.setSeedingStrategy(createSeedingStrategy());
    factorization.setOrdered(ordered);

    factorization.compute();

    return factorization;
  }
}
