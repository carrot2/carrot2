package org.carrot2.clustering.lingo;

import org.carrot2.core.attribute.Processing;
import org.carrot2.matrix.factorization.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

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
    public MatrixFactorizationFactory factorizationFactory = new PartialSingularValueDecompositionFactory();

    /**
     * Performs the reduction.
     */
    void reduce(LingoProcessingContext context)
    {
        context.reducedTdMatrix = factorizationFactory.factorize(context.tdMatrix).getU();
    }
}
