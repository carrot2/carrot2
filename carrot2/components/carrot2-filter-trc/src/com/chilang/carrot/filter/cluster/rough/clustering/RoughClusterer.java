/**
 * 
 * @author chilang
 * Created 2004-01-15, 11:54:25.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.data.IRContext;
import com.chilang.carrot.filter.cluster.rough.CommonFactory;
import com.chilang.carrot.filter.cluster.rough.trsm.RoughSpace;
import com.chilang.carrot.filter.cluster.rough.measure.Similarity;
import com.chilang.carrot.filter.cluster.rough.measure.SimilarityFactory;


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
