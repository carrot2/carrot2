package org.carrot2.clustering.lingo;

import org.carrot2.text.preprocessing.PreprocessingContext;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Stores intermediate data required during Lingo clustering.
 */
class LingoProcessingContext
{
    /** Preprocessing context */
    final PreprocessingContext preprocessingContext;

    /** Term-document matrix */
    DoubleMatrix2D tdMatrix;

    /**
     * Stem indices corresponding to tdMatrix rows. The size of this array is equal to the
     * number of rows of tdMatrix.
     */
    int [] tdMatrixStemIndices;

    /** Reduced term-document matrix */
    DoubleMatrix2D reducedTdMatrix;
    
    LingoProcessingContext(PreprocessingContext preprocessingContext)
    {
        this.preprocessingContext = preprocessingContext;
    }
}
