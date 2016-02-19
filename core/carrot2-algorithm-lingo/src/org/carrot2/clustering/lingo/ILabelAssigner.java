
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

package org.carrot2.clustering.lingo;

import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.text.preprocessing.PreprocessingContext;

import com.carrotsearch.hppc.IntIntHashMap;

/**
 * Assigns labels to base vectors obtained from the factorization.
 */
public interface ILabelAssigner
{
    /**
     * Assigns labels to base vectors found by the matrix factorization. The results must
     * be stored in the {@link LingoProcessingContext#clusterLabelFeatureIndex} and
     * {@link LingoProcessingContext#clusterLabelScore} arrays.
     * 
     * @param context contains all information about the current clustering request
     * @param stemCos base vector -- single stems cosine matrix
     * @param filteredRowToStemIndex mapping between row indices of stemCos and indices of
     *            stems in {@link PreprocessingContext#allStems}
     * @param phraseCos base vector -- phrase cosine matrix
     */
    public void assignLabels(LingoProcessingContext context, DoubleMatrix2D stemCos,
        final IntIntHashMap filteredRowToStemIndex, DoubleMatrix2D phraseCos);
}
