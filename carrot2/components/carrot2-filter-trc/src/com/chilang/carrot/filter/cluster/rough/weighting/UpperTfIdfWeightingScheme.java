
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

import cern.colt.bitvector.BitVector;
import com.chilang.util.MatrixUtils;


/**
 * TD*IDF weighting scheme extended for upper approximation of documents
 */
public class UpperTfIdfWeightingScheme extends TfIdfWeighting implements ApproximateWeightingScheme {

    protected double[][] upperWeight;

    /**
     * Construct weighting scheme that supports upper approximation :
     *   upper approximation of document is appropriately weighted
     * @param termFrequency document-term frequency matrix
     * cell[i,j] contains frequency of term j in document i
     * @param upperApproximationMatrix array of bit vector representing
     * upper approximation binary matrix
     * cell[i,j] = 1 iff term j belongs to upper approximation of document i; 0 - otherwise
     */
    public UpperTfIdfWeightingScheme(int[][] termFrequency,
                                     BitVector[] upperApproximationMatrix) {
        super(termFrequency);
        upperWeight =
                calculateUpperWeight(getTermWeight(), upperApproximationMatrix, getIDF());

        MatrixUtils.normalizeRows(upperWeight);
//        MatrixUtils.compareMatrix(documentTermWeight, upperWeight);
    }

    public double[][] getUpperWeight() {
        return upperWeight;
    }

    /**
     * Calculate weight for upper approximation of document
     * @param weight document-term weight matrix
     * @param upperApproximation binary matrix of upper approximation
     * (cell[i,j] is set iff term j belongs to upper approximation of document i
     * @param idf inverse document frequency array for terms
     * @return weight matrix of document upper approximation (row = document vector)
     */
    protected static double[][] calculateUpperWeight(double[][] weight,
                                                     BitVector[] upperApproximation,
                                                     double[] idf) {
        int rows = weight.length, cols = weight[0].length;
        double[][] upper = new double[rows][cols];
        for(int i=0; i<rows; i++) {
            //first pass
            //copy original weight and remember min weight
            double minWeight = Double.MAX_VALUE;
            for(int j=0; j<cols; j++) {
                double d = weight[i][j];
                upper[i][j] = d;
                if ((d > 0) && (d < minWeight))
                    minWeight = d;
            }
            //second pass assign weight for term not in doc but in upper doc
            for(int j=0; j < cols; j++) {


                if (upperApproximation[i].getQuick(j)) {
                    //term occurs in upper approximation
                    if (!(weight[i][j] > 0)) {
                        //but doesn't occurs in doc itself (weight == 0)

                        // upper_weight = min_weight_of_term_in_doc * IDF / (1 + IDF)
                        upper[i][j] = minWeight * idf[j] / (1 + idf[j]);
                    }
                }
            }
        }
        return upper;
    }
}
