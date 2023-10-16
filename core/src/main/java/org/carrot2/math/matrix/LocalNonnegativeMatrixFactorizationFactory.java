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
 * Performs matrix factorization using the Local Non-negative Matrix Factorization algorithm with
 * minimization of the Kullback-Leibler divergence between A and UV' and multiplicative updating.
 */
public class LocalNonnegativeMatrixFactorizationFactory
    extends IterativeMatrixFactorizationFactory {
  public MatrixFactorization factorize(DoubleMatrix2D A) {
    LocalNonnegativeMatrixFactorization factorization = new LocalNonnegativeMatrixFactorization(A);
    factorization.setK(k);
    factorization.setMaxIterations(maxIterations);
    factorization.setStopThreshold(stopThreshold);
    factorization.setSeedingStrategy(createSeedingStrategy());
    factorization.setOrdered(ordered);
    factorization.compute();
    return factorization;
  }
}
