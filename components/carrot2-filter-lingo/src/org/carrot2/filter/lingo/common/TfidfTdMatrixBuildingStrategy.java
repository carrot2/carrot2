
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.common;

/**
 * @author stachoo
 */
public class TfidfTdMatrixBuildingStrategy implements TdMatrixBuildingStrategy {
    /** */

    /** DOCUMENT ME! */
    private static final int DEFAULT_MINIMUM_TD = 3;

    /** */

    /** DOCUMENT ME! */
    private static final int DEFAULT_MAXIMUM_SIZE = -1; // unlimited

    /** */

    /** DOCUMENT ME! */
    private int minimumTd;

    /** DOCUMENT ME! */
    private int maximumSize;

    /**
     *
     */
    public TfidfTdMatrixBuildingStrategy() {
        this(DEFAULT_MINIMUM_TD);
    }

    /**
     * @param minimumTd
     */
    public TfidfTdMatrixBuildingStrategy(int minimumTd) {
        this(minimumTd, DEFAULT_MAXIMUM_SIZE);
    }

    /**
     * @param minimumTd
     */
    public TfidfTdMatrixBuildingStrategy(int minimumTd, int maximumSize) {
        this.minimumTd = minimumTd;
        this.maximumSize = maximumSize;
    }

    public double[][] buildTdMatrix(AbstractClusteringContext clusteringContext) {
        Feature[] features = clusteringContext.getFeatures();

        // Determine the last feature to be considered
        int rows = 0;
        int size = 0;

        while ((features.length > rows) && !features[rows].isStopWord() &&
                (features[rows].getTf() >= minimumTd) && (features[rows].getLength() == 1) &&
                ((maximumSize < 1) || (size <= maximumSize))) {
            rows++;
            size += clusteringContext.getSnippets().length;
        }

        // Create TD matrix
        double[][] tdMatrix = new double[rows][clusteringContext.getSnippets().length];

        for (int term = 0; term < tdMatrix.length; term++) {
            int[] snippetIndices = features[term].getSnippetIndices();

            for (int doc = 0; doc < snippetIndices.length; doc++) {
                tdMatrix[term][snippetIndices[doc]] = features[term].getSnippetTf()[snippetIndices[doc]] * features[term].getIdf(); // idf
            }
        }

        return tdMatrix;
    }
}
