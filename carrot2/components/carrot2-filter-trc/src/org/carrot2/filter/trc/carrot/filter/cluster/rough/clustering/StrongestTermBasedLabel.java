
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.IRContext;
import org.carrot2.filter.trc.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * Generate label from cluster's strongest terms
 */
public class StrongestTermBasedLabel implements ClusterLabelGenerator {

    Map clusterLabels;

    int numberOfTerm;

    double[][] clusterRepresentation;

    IRContext context;

    /**
     * Construct a generator that choose defined number of strongest term as a cluster label
     * 
     * @param context               document context
     * @param clusterRepresentation representation of cluster as vectors
     * @param numberOfTerm          number of term to be chosen for label
     */
    public StrongestTermBasedLabel(IRContext context,
                                   double[][] clusterRepresentation,
                                   int numberOfTerm) {
        this.context = context;
        this.clusterRepresentation = clusterRepresentation;
        this.numberOfTerm = numberOfTerm;
    }


    public String[] getLabel(int id) {
        return getStrongestTerm(clusterRepresentation[id], numberOfTerm);
    }

    /**
     * Return content of m strongest term (with heaviest weight) in given cluster vector
     * 
     * @param clusterVector vector of term weights in a cluster
     * @param m             
     */
    protected String[] getStrongestTerm(double[] clusterVector, int m) {
        int[] sorted = ArrayUtils.getSortedIndices(clusterVector);
        Collection terms = new ArrayList();
        int j = 0;
        for (int i = clusterVector.length - 1; j < m; i--, j++) {
            double weight = clusterVector[sorted[i]];
            if (weight == 0)
                break;
            terms.add(context.getTermArray()[sorted[i]].getOriginalTerm());
        }

        return (String[]) terms.toArray(new String[0]);
    }
}
