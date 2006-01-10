
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

package com.chilang.carrot.filter.cluster.rough.weighting;

import com.chilang.util.MathUtils;
import com.chilang.util.MatrixUtils;

/**
 * Calculate term weigh as a TF*IDF i.e. :
 *   w_ij = tf_ij * log(N/d_j)
 *
 * where :
 *   w_ij   - weight for term j in document i
 *   tf_ij  - frequency of term j in document i
 *   N      - number of documents in the corpus
 *   d_j    - number of documents containing term j in the corpus
 */
public class TfIdfWeighting implements WeightingScheme{




    protected int[][] documentTermFrequency;

    //document frequency of terms
    // (store separately from frequency matrixfor efficient access)
    protected int[] documentFrequency;

    protected double[] inverseDocumentFrequency;

    protected double[][] documentTermWeight;

    protected TfIdfWeighting() { }


    public TfIdfWeighting(int[][] documentTermFrequencyMatrix) {
        documentTermFrequency = documentTermFrequencyMatrix;
        documentTermWeight = calculateWeightMatrix(documentTermFrequency);

        MatrixUtils.normalizeRows(documentTermWeight);
    }

    private double[][] calculateWeightMatrix(int[][] tdf) {
        int ndoc = tdf.length,
            nterm = tdf[0].length;

        documentFrequency = calculateDocumentFrequency(tdf);
        inverseDocumentFrequency = calculateInverseDocumentFrequency(documentFrequency, ndoc);

        double[][] weigth = new double[ndoc][nterm];


        for (int i=0; i<ndoc; i++) {
            for (int j=0; j<nterm; j++) {
                weigth[i][j] = tdf[i][j] * inverseDocumentFrequency[j];
            }
        }
        return weigth;
    }

    /**
     * Calculate document frequency of terms
     * (number of document containing given term in the corpus
     * @param tdFreq term x document frequency matrix
     * @return array of term's document frequency in the corpus
     */
    protected static int[] calculateDocumentFrequency(int[][] tdFreq) {
        int[] df = new int[tdFreq[0].length];
        for(int i=0; i<tdFreq.length; i++) {
            for (int j=0; j<tdFreq[i].length; j++) {
                if (tdFreq[i][j] >0)
                    df[j]++;
            }
        }
        return df;
    }

    protected static double[] calculateInverseDocumentFrequency(int[] df, int ndoc) {
        int size = df.length;
        double[] idf = new double[size];
        for (int i = 0; i < size; i++) {
            idf[i] =  MathUtils.log10((double)ndoc/df[i]);
        }
        return idf;
    }
    public double[][] getTermWeight() {
        return documentTermWeight;
    }

    protected double[] getIDF() {
        return inverseDocumentFrequency;
    }
}
