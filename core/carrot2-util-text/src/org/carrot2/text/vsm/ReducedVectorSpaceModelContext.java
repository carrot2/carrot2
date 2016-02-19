
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

import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

/**
 * Stores data related a Vector Space Model with reduced dimensionality.
 * 
 * @see TermDocumentMatrixReducer
 */
public class ReducedVectorSpaceModelContext
{
    /** Vector Space Model context for the underlying documents. */
    public final VectorSpaceModelContext vsmContext;

    /** Base vectors of the reduced term document matrix */
    public DoubleMatrix2D baseMatrix;

    /** Coefficient vectors of the reduced term document matrix */
    public DoubleMatrix2D coefficientMatrix;
    
    public ReducedVectorSpaceModelContext(VectorSpaceModelContext vectorSpaceModelContext)
    {
        this.vsmContext = vectorSpaceModelContext;
    }
}
