
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.stachoodev.carrot.filter.lingo.lsicluster;

import Jama.Matrix;

import com.stachoodev.carrot.filter.lingo.common.*;
import com.stachoodev.carrot.filter.lingo.util.matrix.MatrixUtils;

import java.util.Arrays;


/**
 *
 */
public class DummyClusteringStrategy implements ClusteringStrategy {
    /**
     * @see com.stachoodev.carrot.filter.lingo.common.ClusteringStrategy#cluster(com.stachoodev.carrot.filter.lingo.common.ClusteringContext)
     */
    public Cluster[] cluster(AbstractClusteringContext clusteringContext) {
        Snippet[] snippets = clusteringContext.getSnippets();
        Feature[] features = clusteringContext.getFeatures();

        // Find where the phrases start
        int firstPhrase = 0;

        while (features[firstPhrase].getLength() < 2) {
            firstPhrase++;
        }

        for (int i = 0; i < features.length; i++) {
            if (((features[i].getLength() > 1) && (features[i].getTf() > 1)) ||
                    ((features[i].getTf() > 1) && !features[i].isStopWord())) {
                System.out.println(i + ": " + features[i]);
            }
        }

        // Create TD matrix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy(2);

        Matrix tdMatrix = new Matrix(tdMatrixBuildingStrategy.buildTdMatrix(
                    clusteringContext));
        MatrixUtils.normalizeColumnLengths(tdMatrix);

        int termCount = tdMatrix.getRowDimension();
        int clusterCount = 2;

        if (tdMatrix.getColumnDimension() > tdMatrix.getRowDimension()) {
            tdMatrix = tdMatrix.transpose();
        }

        Matrix U = getReducedTdMatrix(tdMatrix, clusterCount, 5);
        Matrix V = (U.transpose()).times(tdMatrix).transpose();
        MatrixUtils.normalizeColumnLengths(V);

        /*
           // The SVD
           SingularValueDecomposition svd = tdMatrix.svd();
              Matrix U = null;
              Matrix V = null;
              if (transposed)
              {
                  V = svd.getU();
                  U = svd.getV();
                  tdMatrix = tdMatrix.transpose();
              }
              else
              {
                  U = svd.getU();
                  V = svd.getV();
              }
         */
        //		tdMatrix.print(4, 2);
        V.getMatrix(0, V.getRowDimension() - 1, 0, clusterCount - 1).print(5, 2);
        U.getMatrix(0, U.getRowDimension() - 1, 0, clusterCount - 1).print(5, 2);

        // Create clusters
        Cluster[] clusters = new Cluster[clusterCount];

        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new Cluster();
        }

        // Find best phrases
        int[] phraseIndex = new int[clusterCount];
        double[] phraseCos = new double[clusterCount];
        Arrays.fill(phraseCos, -1);

        for (int j = 0; j < V.getRowDimension(); j++) {
            int c = 0;
            double max = V.get(j, c);

            for (int i = c + 1; i < clusterCount; i++) {
                if (Math.abs(V.get(j, i)) > max) {
                    max = Math.abs(V.get(j, i));
                    c = i;
                }
            }

            clusters[c].addSnippet(snippets[j]);
        }

        Matrix b = U.getMatrix(0, U.getRowDimension() - 1, 0, clusterCount - 1);

        //		MatrixUtils.normalizeColumnLengths(b);
        if (firstPhrase < features.length) // we need some phrases
         {
            // Prepare phrase matrix
            Matrix phraseMatrix = new Matrix(termCount,
                    features.length - firstPhrase);

            for (int p = firstPhrase; p < features.length; p++) {
                int[] featureIndices = features[p].getPhraseFeatureIndices();

                for (int f = 0; f < featureIndices.length; f++) {
                    if (featureIndices[f] < termCount) // skip stopwords
                     {
                        phraseMatrix.set(featureIndices[f], p - firstPhrase,
                            phraseMatrix.get(featureIndices[f], p -
                                firstPhrase) + 1);
                    }
                }
            }

            MatrixUtils.normalizeColumnLengths(phraseMatrix);

            Matrix cos = phraseMatrix.transpose().times(b);

            //			cos.print(4, 2);
            // Choose best phrases
            for (int c = 0; c < clusterCount; c++) {
                phraseCos[c] = Math.abs(cos.get(0, c));
                phraseIndex[c] = 0 + firstPhrase;

                for (int p = 1; p < cos.getRowDimension(); p++) {
                    if (Math.abs(cos.get(p, c)) > phraseCos[c]) {
                        phraseCos[c] = Math.abs(cos.get(p, c));
                        phraseIndex[c] = p + firstPhrase;
                    }
                }
            }
        }

        // Find best single terms
        for (int c = 0; c < clusterCount; c++) {
            int labelCount = features[phraseIndex[c]].getLength();
            double[] maxs = new double[labelCount];
            int[] labels = new int[labelCount];

            Arrays.fill(maxs, -20);
            Arrays.fill(labels, 0);

            for (int i = 0; i < U.getRowDimension(); i++) {
                for (int l = 0; l < labelCount; l++) {
                    if (maxs[l] < Math.abs(U.get(i, c))) // && 
                    //						!contains(features[phraseIndex[c]].getPhraseFeatureIndices(), i))
                     {
                        maxs[l] = Math.abs(U.get(i, c));
                        labels[l] = i;

                        break;
                    }
                }
            }

            double cos = 0;

            for (int l = 0; l < labels.length; l++) {
                cos += b.get(labels[l], c);
                clusters[c].addLabel(features[labels[l]].getText());
            }

            cos = cos / Math.sqrt(labels.length);

            if (phraseCos[c] > cos) {
                clusters[c].addLabel("*\"" +
                    features[phraseIndex[c]].getText() + "\"*");
            } else {
                clusters[c].addLabel("\"" + features[phraseIndex[c]].getText() +
                    "\"");
            }

            clusters[c].addLabel("(" + clusters[c].getSnippets().length + ")");
        }

        return clusters;
    }

    /**
     * @param tdMatrix
     *
     * @return Matrix
     */
    private Matrix getReducedTdMatrix(Matrix tdMatrix, int k, int q) {
        Matrix R = new Matrix(tdMatrix.getArrayCopy());
        Matrix Rs = new Matrix(tdMatrix.getRowDimension(),
                tdMatrix.getColumnDimension());
        Matrix B = new Matrix(tdMatrix.getRowDimension(), k);
        Matrix b;

        for (int i = 0; i < k; i++) {
            scaleColumns(R, Rs, q);

            b = Rs.times(Rs.transpose()).eig().getV().transpose().getMatrix(0,
                    Rs.getRowDimension() - 1, 0, 0);

            for (int j = 0; j < B.getRowDimension(); j++) {
                B.set(j, i, b.get(j, 0));
            }

            R = R.minus(b.times(b.transpose()).times(R));
        }

        return B;
    }

    /**
     * @param R
     * @param q
     */
    private void scaleColumns(Matrix R, Matrix Rs, int q) {
        for (int c = 0; c < R.getColumnDimension(); c++) {
            // Calculate Euclidean length
            double len = 0;

            for (int r = 0; r < R.getRowDimension(); r++) {
                len += (R.get(r, c) * R.get(r, c));
            }

            len = Math.sqrt(len);

            // Scale
            len = 1; //Math.pow(len, (double)q);

            for (int r = 0; r < R.getRowDimension(); r++) {
                Rs.set(r, c, R.get(r, c) * len);
            }
        }
    }

    /**
     *
     */
    private int calculateClusterCount(Matrix tdMatrix) {
        int m = tdMatrix.getRowDimension();
        int n = tdMatrix.getColumnDimension();
        ;

        // Eigenvalues
        double[] lambda = tdMatrix.transpose().times(tdMatrix)
                                  .times(1.0 /m).eig()
                                  .getRealEigenvalues();

        int k = 0;
        double mdl = -1;

        // Find i such that mdl is at its minimum
        for (int i = 0; i < n; i++) {
            // Sum (numerator)
            double sum = 0;

            for (int j = i + 1; j <= n; j++) {
                sum += lambda[j - 1];
            }

            double numerator = sum / (n - i);

            // Product
            double product = 1;

            for (int j = i + 1; j <= n; j++) {
                product *= lambda[j - 1];
            }

            // Denominator
            double denominator = Math.pow(product, 1.0 / (n - i));

            // MDL
            double currentMdl = (m * (n - i) * (Math.log(numerator / denominator))) +
                ((i * ((2 * n) - i + 1) * Math.log(m)) / 2);

            System.out.println(i + " " + currentMdl + " " + denominator + " " +
                numerator);

            if (i == 0) {
                mdl = currentMdl;
            } else {
                if (currentMdl < mdl) {
                    mdl = currentMdl;
                    k = i;
                }
            }
        }

        System.out.println("k = " + k);

        return 10;
    }

    /**
     * DOCUMENT ME!
     *
     * @param array DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean contains(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }

        return false;
    }
}
