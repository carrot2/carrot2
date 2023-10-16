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

import org.carrot2.attrs.AttrComposite;
import org.carrot2.math.mahout.matrix.DoubleMatrix2D;

/** Performs matrix factorization using the Singular Value Decomposition algorithm. */
public class PartialSingularValueDecompositionFactory extends AttrComposite
    implements MatrixFactorizationFactory {
  /** The desired number of base vectors */
  protected int k;

  /** The default desired number of base vectors */
  protected static final int DEFAULT_K = -1;

  /**
   * Creates the factory that creates factorizations that compute the maximum number of base
   * vectors.
   */
  public PartialSingularValueDecompositionFactory() {
    this.k = DEFAULT_K;
  }

  public MatrixFactorization factorize(DoubleMatrix2D A) {
    PartialSingularValueDecomposition partialSingularValueDecomposition =
        new PartialSingularValueDecomposition(A);

    partialSingularValueDecomposition.setK(k);
    partialSingularValueDecomposition.compute();

    return partialSingularValueDecomposition;
  }

  /**
   * Sets the number of base vectors <i>k </i>.
   *
   * @param k the number of base vectors
   */
  public void setK(int k) {
    this.k = k;
  }

  /** Returns the number of base vectors <i>k </i>. */
  public int getK() {
    return k;
  }
}
