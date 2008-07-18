package org.carrot2.clustering.lingo;

import org.carrot2.core.attribute.Processing;
import org.carrot2.matrix.MatrixUtils;
import org.carrot2.matrix.factorization.*;
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
     * Desired cluster count.
     * 
     * @level Medium
     * @group Matrix model
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2)
    public int desiredClusterCount = 20;

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
                .setK(desiredClusterCount);
        }

        MatrixUtils.normalizeColumnL2(context.tdMatrix, null);
        context.baseMatrix = factorizationFactory.factorize(context.tdMatrix).getU();

        if (!(factorizationFactory instanceof IterativeMatrixFactorizationFactory)
            && context.baseMatrix.columns() > desiredClusterCount)
        {
            context.baseMatrix = context.baseMatrix.viewPart(0, 0, context.baseMatrix
                .rows(), desiredClusterCount);
        }
    }
}
