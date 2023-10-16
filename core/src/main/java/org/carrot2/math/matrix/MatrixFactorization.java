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
 * For an <i>m</i> &times; <i>n</i> matrix <i>A</i> and given <i>k</i>, computes an <i>m </i>
 * &times; <i>k</i> matrix <i>U</i> and <i>k</i> &times; <i>n</i> matrix <i>V'</i> such that <i>A ~=
 * UV'</i>.
 */
public interface MatrixFactorization {
  /**
   * Returns the U matrix (base vectors matrix).
   *
   * @return U matrix
   */
  DoubleMatrix2D getU();

  /**
   * Returns the V matrix (coefficient matrix)
   *
   * @return V matrix
   */
  DoubleMatrix2D getV();
}
