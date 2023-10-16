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

import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.math.mahout.matrix.*;

/** A factory of {@link MatrixFactorization}s. */
public interface MatrixFactorizationFactory extends AcceptingVisitor {
  /**
   * Factorizes matrix <code>A</code>.
   *
   * @param A matrix to be factorized.
   */
  MatrixFactorization factorize(DoubleMatrix2D A);
}
