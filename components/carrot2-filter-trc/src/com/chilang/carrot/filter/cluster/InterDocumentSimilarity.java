
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

package com.chilang.carrot.filter.cluster;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import com.chilang.carrot.filter.cluster.rough.measure.Similarity;
import com.chilang.carrot.filter.cluster.rough.measure.SimilarityFactory;
import com.chilang.util.MatrixUtils;

/**
 * Wrapper for inter-document similarity matrix
 */
public class InterDocumentSimilarity {


    DoubleMatrix2D similarityMatrix;

    /**
     * Construct inter-document similarity matrix for set of documents
     * represented as given document-term matrix
     * using given similarity measure
     * @param documentTermWeight
     * @param measure
     */
    public InterDocumentSimilarity(double[][] documentTermWeight, Similarity measure) {
//        DoubleMatrix2D representation = DoubleFactory2D.dense.make(documentTermWeight);
//        similarityMatrix = Statistic.distance(representation.viewDice(), measure);
        similarityMatrix = DoubleFactory2D.dense.make(SimilarityFactory.distance(documentTermWeight, measure));
    }

    /**
     * Return number of unique entries in similarity matrix
     * (i.e. cells in matrix upper diagonal)
     */
    public int size() {
        return (similarityMatrix.size() - similarityMatrix.rows()) / 2;
    }

    /**
     * Return number of non-zero entries (only from upper diagonal)
     */
    public int nonZero() {
        return similarityMatrix.cardinality() / 2;
    }

    /**
     * Return internal representation as matrix
     */
    public DoubleMatrix2D getInternalMatrix() {
        return similarityMatrix;
    }


    /**
     * Get entries (only upper diagonal) of matrix as an array
     */
    public DoubleMatrix1D getAsSeries() {
        return MatrixUtils.upperDiagonalToVector(similarityMatrix);
    }

    /**
     * Return pairs of documents that has similarity greater than given threshold
     * @param threshold
     * @return array of pairs of document indices
     * ([i][0] - first document's index, [i][1] second document's index
     */
    public int[][] getSimilarOver(double threshold) {
        int size = similarityMatrix.columns();
        int[][] indices = new int[size()][2];
        int counter = 0;
        for(int i=0; i < size; i++) {
            for(int j=i+1; j < size; j++) {
                if (similarityMatrix.getQuick(i,j) > threshold) {
                    indices[counter][0] = i;
                    indices[counter][1] = j;
                    counter++;
                }
            }
        }
        int[][] tmp = new int[counter][2];
        System.arraycopy(indices, 0, tmp, 0, counter);
        return tmp;
    }

    /**
     * Return entries between range (min, max]
     */
    public int[][] getSimilarWithinRange(double min, double max) {
        int size = similarityMatrix.columns();
        int[][] indices =  new int[size()][2];
        int counter = 0;
        for(int i=0; i < size; i++) {
            for(int j=i+1; j < size; j++) {
                double value = similarityMatrix.getQuick(i,j);
                if ((value > min) && (value <= max)) {
                    indices[counter][0] = i;
                    indices[counter][1] = j;
                    counter++;
                }
            }
        }
        int[][] tmp = new int[counter][2];
        System.arraycopy(indices, 0, tmp, 0, counter);
        return tmp;
    }


}
