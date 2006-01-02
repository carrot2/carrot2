
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.data.IRContext;
import com.chilang.carrot.filter.cluster.rough.CommonFactory;
import com.chilang.carrot.filter.cluster.rough.trsm.RoughSpace;
import com.chilang.carrot.filter.cluster.rough.measure.Similarity;


/**
 * Implementation of K-means algorithm with
 * upper approximation for document representation
 */
public class RoughClusterer extends KClusterer{

    protected int cooccurrenceThreshold;

    RoughSpace roughSpace;

    public RoughClusterer(int k, double membershipThreshold,
                          Similarity measure, int cooccurrenceThreshold, boolean phrase) {
        super(k, membershipThreshold, measure, phrase);
        this.cooccurrenceThreshold = cooccurrenceThreshold;
    }

    public void cluster() {
        if (!contextInitialized)
            throw new IllegalStateException("Context must be initialized first.");
        if (clusteringPerformed)
            throw new IllegalStateException("Clustering has been executed already.");

        algorithm =
                new SoftKMeansWithMerging(
                        roughSpace.getUpperWeight(),
                        numberOfClusters,
                        membershipThreshold,
                        similarity,
                        0.9);

        algorithm.cluster();

//        System.out.println("Clustering executed.");
        clusteringPerformed = true;
    }


    public void setContext(IRContext context) {
        this.context = context;
        this.context.buildDocumentTermMatrix();
        roughSpace = CommonFactory.createRoughSpace(context.getTermFrequency(), cooccurrenceThreshold, 0);
        clusteringPerformed = false;
        contextInitialized = true;
    }

}
