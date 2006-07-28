
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix.factorization;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PartialSingularValueDecompositionFactory implements
    MatrixFactorizationFactory
{
    /** The desired number of base vectors */
    protected int k;
    protected static int DEFAULT_K = -1;

    /**
     *  
     */
    public PartialSingularValueDecompositionFactory()
    {
        this.k = DEFAULT_K;
    }

    /*
     * (non-Javadoc)
     */
    public MatrixFactorization factorize(DoubleMatrix2D A)
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
     * 
     */
    public int getK()
    {
        return k;
    }
}