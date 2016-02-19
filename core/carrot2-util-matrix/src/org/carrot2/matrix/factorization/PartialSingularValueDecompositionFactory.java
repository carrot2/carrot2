
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

package org.carrot2.matrix.factorization;

import org.carrot2.mahout.math.matrix.*;

/**
 * Factory for {@link PartialSingularValueDecomposition}s.
 */
public class PartialSingularValueDecompositionFactory implements
    IMatrixFactorizationFactory
{
    /** The desired number of base vectors */
    protected int k;

    /** The default desired number of base vectors */
    protected static final int DEFAULT_K = -1;

    /**
     * Creates the factory that creates factorizations that compute the maximum number of
     * base vectors.
     */
    public PartialSingularValueDecompositionFactory()
    {
        this.k = DEFAULT_K;
    }

    public IMatrixFactorization factorize(DoubleMatrix2D A)
    {
        PartialSingularValueDecomposition partialSingularValueDecomposition = new PartialSingularValueDecomposition(
            A);

        partialSingularValueDecomposition.setK(k);
        partialSingularValueDecomposition.compute();

        return partialSingularValueDecomposition;
    }

    /**
     * Sets the number of base vectors <i>k </i>.
     * 
     * @param k the number of base vectors
     */
    public void setK(int k)
    {
        this.k = k;
    }

    /**
     * Returns the number of base vectors <i>k </i>.
     */
    public int getK()
    {
        return k;
    }
}
