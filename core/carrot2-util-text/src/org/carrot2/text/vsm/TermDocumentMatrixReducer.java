
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.vsm;

import org.carrot2.core.attribute.Processing;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.matrix.MatrixUtils;
import org.carrot2.matrix.factorization.IMatrixFactorization;
import org.carrot2.matrix.factorization.IMatrixFactorizationFactory;
import org.carrot2.matrix.factorization.IterationNumberGuesser;
import org.carrot2.matrix.factorization.IterationNumberGuesser.FactorizationQuality;
import org.carrot2.matrix.factorization.IterativeMatrixFactorizationFactory;
import org.carrot2.matrix.factorization.KMeansMatrixFactorizationFactory;
import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
import org.carrot2.matrix.factorization.NonnegativeMatrixFactorizationEDFactory;
import org.carrot2.matrix.factorization.NonnegativeMatrixFactorizationKLFactory;
import org.carrot2.matrix.factorization.PartialSingularValueDecompositionFactory;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * Reduces the dimensionality of a term-document matrix using a matrix factorization
 * algorithm.
 */
@Bindable(prefix = "TermDocumentMatrixReducer")
public class TermDocumentMatrixReducer
{
    /**
     * Factorization method. The method to be used to factorize the term-document matrix
     * and create base vectors that will give rise to cluster labels.
     */
    @Input
    @Processing
    @Attribute
    @Required
    @ImplementingClasses(classes =
    {
        PartialSingularValueDecompositionFactory.class,
        NonnegativeMatrixFactorizationEDFactory.class,
        NonnegativeMatrixFactorizationKLFactory.class,
        LocalNonnegativeMatrixFactorizationFactory.class,
        KMeansMatrixFactorizationFactory.class
    }, strict = false)
    @Label("Factorization method")
    @Level(AttributeLevel.ADVANCED)
    @Group(TermDocumentMatrixBuilder.MATRIX_MODEL)
    public IMatrixFactorizationFactory factorizationFactory = new NonnegativeMatrixFactorizationEDFactory();

    /**
     * Factorization quality. The number of iterations of matrix factorization to perform.
     * The higher the required quality, the more time-consuming clustering.
     */
    @Input
    @Processing
    @Required
    @Attribute
    @Label("Factorization quality")
    @Level(AttributeLevel.ADVANCED)
    @Group(TermDocumentMatrixBuilder.MATRIX_MODEL)
    public FactorizationQuality factorizationQuality = FactorizationQuality.HIGH;

    /**
     * Performs the reduction.
     */
    public void reduce(ReducedVectorSpaceModelContext context, int dimensions)
    {
        final VectorSpaceModelContext vsmContext = context.vsmContext;
        if (vsmContext.termDocumentMatrix.columns() == 0
            || vsmContext.termDocumentMatrix.rows() == 0)
        {
            context.baseMatrix = new DenseDoubleMatrix2D(
                vsmContext.termDocumentMatrix.rows(),
                vsmContext.termDocumentMatrix.columns());
            return;
        }

        if (factorizationFactory instanceof IterativeMatrixFactorizationFactory)
        {
            ((IterativeMatrixFactorizationFactory) factorizationFactory).setK(dimensions);
            IterationNumberGuesser.setEstimatedIterationsNumber(
                (IterativeMatrixFactorizationFactory) factorizationFactory,
                vsmContext.termDocumentMatrix, factorizationQuality);
        }

        MatrixUtils.normalizeColumnL2(vsmContext.termDocumentMatrix, null);
        final IMatrixFactorization factorization = factorizationFactory
            .factorize(vsmContext.termDocumentMatrix);
        context.baseMatrix = factorization.getU();
        context.coefficientMatrix = factorization.getV();

        context.baseMatrix = trim(factorization.getU(), dimensions);
        context.coefficientMatrix = trim(factorization.getV(), dimensions);
    }

    private final DoubleMatrix2D trim(DoubleMatrix2D matrix, int dimensions)
    {
        if (!(factorizationFactory instanceof IterativeMatrixFactorizationFactory)
            && matrix.columns() > dimensions)
        {
            return matrix.viewPart(0, 0, matrix.rows(), dimensions);
        }
        else
        {
            return matrix;
        }
    }
}
