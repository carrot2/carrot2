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
@Bindable
public class TermDocumentMatrixReducer
{
    /**
     * Factorization method.
     * 
     * @level Advanced
     * @group Matrix model
     */
    @Input
    @Processing
    @Attribute
    @ImplementingClasses(classes =
    {
        PartialSingularValueDecompositionFactory.class,
        NonnegativeMatrixFactorizationEDFactory.class,
        NonnegativeMatrixFactorizationKLFactory.class,
        LocalNonnegativeMatrixFactorizationFactory.class,
        KMeansMatrixFactorizationFactory.class
    })
    public MatrixFactorizationFactory factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();

    /**
     * Desired cluster count base.
     * 
     * @level Medium
     * @group Matrix model
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2, max = 100)
    public int desiredClusterCountBase = 20;

    /**
     * Factorization quality.
     * 
     * @level Advanced
     * @group Matrix model
     */
    @Input
    @Processing
    @Attribute
    public FactorizationQuality factorizationQuality = FactorizationQuality.HIGH;

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
