package org.carrot2.clustering.lingo;

import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.PreprocessingContext.AllLabels;

import bak.pcj.map.IntKeyIntMap;
import bak.pcj.set.IntBitSet;

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

    /** Stem index to row index mapping for the tdMatrix */
    IntKeyIntMap tdMatrixStemToRowIndex;

    /** Base vectors for the tdMatrix */
    DoubleMatrix2D baseMatrix;

    /** Feature indices (like in {@link AllLabels#featureIndex}) that should form clusters */
    int [] clusterLabelFeatureIndex;

    /** Scores for cluster labels */
    double [] clusterLabelScore;

    /** Documents assigned to clusters */
    IntBitSet [] clusterDocuments;

    /**
     * Term-document-like matrix for phrases from {@link AllLabels}. If there are no
     * phrases in {@link AllLabels}, phrase matrix is <code>null</code>.
     */
    DoubleMatrix2D phraseMatrix;

    /**
     * Index of the first phrase in {@link AllLabels}, or -1 if there are no phrases in
     * {@link AllLabels}.
     */
    int firstPhraseIndex = -1;

    LingoProcessingContext(PreprocessingContext preprocessingContext)
    {
        this.preprocessingContext = preprocessingContext;
    }
}
