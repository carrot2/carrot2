/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;

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
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorizationFactory#factorize(cern.colt.matrix.DoubleMatrix2D)
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
     * @return
     */
    public int getK()
    {
        return k;
    }
}