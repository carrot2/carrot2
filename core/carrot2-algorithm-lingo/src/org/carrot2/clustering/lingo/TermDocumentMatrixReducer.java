
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.lingo;

import org.carrot2.core.attribute.Processing;
import org.carrot2.matrix.MatrixUtils;
import org.carrot2.matrix.factorization.*;
import org.carrot2.matrix.factorization.IterationNumberGuesser.FactorizationQuality;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

import cern.colt.matrix.DoubleFactory2D;

/**
 * Reduces the dimensionality of a term-document matrix using a matrix factorization
 * algorithm.
 */
@Bindable(prefix = "LingoClusteringAlgorithm")
public class TermDocumentMatrixReducer
{
    /**
     * Factorization method. The method to be used to factorize the term-document matrix
     * and create base vectors that will give rise to cluster labels.
     * 
     * @level Advanced
     * @group Matrix model
     * @label Factorization method
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
    public IMatrixFactorizationFactory factorizationFactory = new NonnegativeMatrixFactorizationEDFactory();

    /**
     * Factorization quality. The number of iterations of matrix factorization to perform.
     * The higher the required quality, the more time-consuming clustering.
     * 
     * @level Advanced
     * @group Matrix model
     * @label Factorization quality
     */
    @Input
    @Processing
    @Attribute
    public FactorizationQuality factorizationQuality = FactorizationQuality.HIGH;

    /**
     * Desired cluster count base. Base factor used to calculate the number of clusters
     * based on the number of documents on input. The larger the value, the more clusters
     * will be created. The number of clusters created by the algorithm will be
     * proportional to the cluster count base, but not in a linear way.
     * 
     * @level Basic
     * @group Clusters
     * @label Cluster count base
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2, max = 100)
    public int desiredClusterCountBase = 40;

    /**
     * Performs the reduction.
     */
    void reduce(LingoProcessingContext context)
    {
        if (context.tdMatrix.columns() == 0 || context.tdMatrix.rows() == 0)
        {
            context.baseMatrix = DoubleFactory2D.dense.make(context.tdMatrix.rows(),
                context.tdMatrix.columns());
            return;
        }

        if (factorizationFactory instanceof IterativeMatrixFactorizationFactory)
        {
            ((IterativeMatrixFactorizationFactory) factorizationFactory)
                .setK(getDesiredClusterCount(context));
            IterationNumberGuesser.setEstimatedIterationsNumber(
                (IterativeMatrixFactorizationFactory) factorizationFactory,
                context.tdMatrix, factorizationQuality);
        }

        MatrixUtils.normalizeColumnL2(context.tdMatrix, null);
        context.baseMatrix = factorizationFactory.factorize(context.tdMatrix).getU();

        if (!(factorizationFactory instanceof IterativeMatrixFactorizationFactory)
            && context.baseMatrix.columns() > desiredClusterCountBase)
        {
            context.baseMatrix = context.baseMatrix.viewPart(0, 0, context.baseMatrix
                .rows(), desiredClusterCountBase);
        }
    }

    /**
     * Calculates the desired cluster count using a very simple model.
     */
    private int getDesiredClusterCount(LingoProcessingContext context)
    {
        final int documentCount = context.preprocessingContext.documents.size();
        return Math.min((int) ((desiredClusterCountBase / 10.0) * Math
            .sqrt(documentCount)), documentCount);
    }
}
