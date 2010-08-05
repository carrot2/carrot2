
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.lingo;

import org.apache.mahout.math.matrix.*;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.PreprocessingContext.AllLabels;
import org.carrot2.text.vsm.VectorSpaceModelContext;

import com.carrotsearch.hppc.BitSet;

/**
 * Stores intermediate data required during Lingo clustering.
 */
@SuppressWarnings("deprecation")
public class LingoProcessingContext
{
    /** Preprocessing context */
    public final PreprocessingContext preprocessingContext;

    /** Vector space model context */
    public final VectorSpaceModelContext vsmContext;
    
    /** Base vectors for the tdMatrix */
    DoubleMatrix2D baseMatrix;

    /** Feature indices (like in {@link AllLabels#featureIndex}) that should form clusters */
    int [] clusterLabelFeatureIndex;

    /** Scores for cluster labels */
    double [] clusterLabelScore;

    /** Documents assigned to clusters */
    BitSet [] clusterDocuments;

    LingoProcessingContext(VectorSpaceModelContext vsmContext)
    {
        this.preprocessingContext = vsmContext.preprocessingContext;
        this.vsmContext = vsmContext;
    }
}
