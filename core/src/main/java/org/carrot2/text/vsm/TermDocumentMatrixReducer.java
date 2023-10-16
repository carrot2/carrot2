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
package org.carrot2.text.vsm;

import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrObject;
import org.carrot2.math.mahout.matrix.DoubleMatrix2D;
import org.carrot2.math.mahout.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.math.matrix.IterativeMatrixFactorizationFactory;
import org.carrot2.math.matrix.MatrixFactorization;
import org.carrot2.math.matrix.MatrixFactorizationFactory;
import org.carrot2.math.matrix.MatrixUtils;
import org.carrot2.math.matrix.NonnegativeMatrixFactorizationEDFactory;

/** Reduces the dimensionality of a term-document matrix using a matrix factorization algorithm. */
public class TermDocumentMatrixReducer extends AttrComposite {
  /**
   * Factorization method. The method to be used to factorize the term-document matrix and create
   * base vectors that will give rise to cluster labels.
   */
  public MatrixFactorizationFactory factorizationFactory;

  {
    attributes.register(
        "factorizationFactory",
        AttrObject.builder(MatrixFactorizationFactory.class)
            .label("Term-document matrix factorization method")
            .getset(() -> factorizationFactory, (v) -> factorizationFactory = v)
            .defaultValue(NonnegativeMatrixFactorizationEDFactory::new));
  }

  /** Performs the reduction. */
  public void reduce(ReducedVectorSpaceModelContext context, int dimensions) {
    final VectorSpaceModelContext vsmContext = context.vsmContext;
    if (vsmContext.termDocumentMatrix.columns() == 0 || vsmContext.termDocumentMatrix.rows() == 0) {
      context.baseMatrix =
          new DenseDoubleMatrix2D(
              vsmContext.termDocumentMatrix.rows(), vsmContext.termDocumentMatrix.columns());
      return;
    }

    if (factorizationFactory instanceof IterativeMatrixFactorizationFactory) {
      ((IterativeMatrixFactorizationFactory) factorizationFactory)
          .estimateIterationsNumber(dimensions, vsmContext.termDocumentMatrix);
    }

    MatrixUtils.normalizeColumnL2(vsmContext.termDocumentMatrix, null);
    final MatrixFactorization factorization =
        factorizationFactory.factorize(vsmContext.termDocumentMatrix);
    context.baseMatrix = factorization.getU();
    context.coefficientMatrix = factorization.getV();
    context.baseMatrix = trim(factorizationFactory, factorization.getU(), dimensions);
    context.coefficientMatrix = trim(factorizationFactory, factorization.getV(), dimensions);
  }

  private final DoubleMatrix2D trim(
      MatrixFactorizationFactory factorizationFactory, DoubleMatrix2D matrix, int dimensions) {
    if (!(factorizationFactory instanceof IterativeMatrixFactorizationFactory)
        && matrix.columns() > dimensions) {
      return matrix.viewPart(0, 0, matrix.rows(), dimensions);
    } else {
      return matrix;
    }
  }
}
