/**
 * 
 * @author chilang
 * Created 2003-12-30, 01:11:30.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.data.IRContext;
import com.chilang.carrot.filter.cluster.rough.measure.Similarity;
import com.chilang.carrot.filter.cluster.rough.measure.SimilarityFactory;
import com.chilang.util.ArrayUtils;
import com.chilang.util.MatrixUtils;

import java.util.Map;


/**
 * Clusterer using KMeans algorithms
 */
public class KClusterer implements Clusterer {

    public static final String K = "k";
    public static final String MEMBERSHIP_THRESHOLD = "membershipThreshold";
    public static final String SIMILARITY_MEASURE = "similarityMeasure";


    protected IRContext context;

    protected int numberOfClusters;
    protected double membershipThreshold;
    protected Similarity similarity;

    protected AbstractKMeansAlgorithm algorithm;

    protected boolean phrase = false;

    /**
     * Indicate if clustering has been excecuted;
     */
    protected boolean clusteringPerformed;
    protected boolean contextInitialized = false;     // flag if context with data has been set

    public KClusterer(IRContext context, Map params) {
        this.context = context;
        parseParameters(params);
    }

    public KClusterer(int k, double membershipThreshold, Similarity measure, boolean phrase) {
        this.numberOfClusters = k;
        this.membershipThreshold = membershipThreshold;
        this.similarity = measure;
        this.phrase = phrase;
    }

    public void setContext(IRContext context) {
        this.context = context;
        this.context.buildDocumentTermMatrix();
        //WARNING multi-thread DANGER
        clusteringPerformed = false;
        contextInitialized = true;
    }


    protected void parseParameters(Map params) {
        this.numberOfClusters = Integer.parseInt((String)params.get(K));
        this.membershipThreshold = Double.parseDouble((String)params.get(MEMBERSHIP_THRESHOLD));
        this.similarity = SimilarityFactory.getSimilarity((String)params.get(SIMILARITY_MEASURE));
    }

    public void cluster() {
        if (clusteringPerformed)
            throw new IllegalStateException("Clustering has been executed already.");

        context.buildDocumentTermMatrix();
        algorithm = new SoftKMeansWithMerging(
                context.getTermWeight(),
                numberOfClusters,
                membershipThreshold,
                similarity,
                0.9);
        algorithm.cluster();
//        System.out.println("Executed");
        clusteringPerformed = true;
    }

    public XCluster[] getClusters() {
        if (!clusteringPerformed)
            throw new IllegalStateException("No clusters available. Execute clustering first!");

        //rows = cluster membership binary vector
//        int[][] clusterMember = MatrixUtils.transpose(algorithm.getClusters());
        double[][] membership = MatrixUtils.transpose(algorithm.getMembership());
        double[][] clusterVector = algorithm.getClusterRepresentation();
//        System.out.println(MatrixUtils.toString(clusterVector));

        int[] nonEmpty = nonEmptyMembershipIndices(membership);
        int resultClusters = nonEmpty.length;
        XCluster[] clusters = new XCluster[resultClusters+1];


        ClusterLabelGenerator labelGenerator;
        if (phrase) {
            labelGenerator = new PhraseBasedLabel(context, 3, membership);
        } else {
            labelGenerator = new StrongestTermBasedLabel(context, clusterVector, 3);
        }

        for (int i = 0; i < resultClusters; i++) {
            String[] label = labelGenerator.getLabel(nonEmpty[i]);
            XCluster.Member[] members = createMembers(membership[nonEmpty[i]]);
            clusters[i] = new XClusterImpl(label,  members);
        }

        clusters[resultClusters] = createOtherCluster();
        return clusters;
//        return null;
    }


    private int[] nonEmptyMembershipIndices(double[][] membership) {
        int[] nonEmpty = new int[membership.length];
        int index = 0;
        for (int i = 0; i < membership.length; i++) {
            double sum = ArrayUtils.sum(membership[i]);
            if (sum > 0) {
                nonEmpty[index++] = i;
            }
        }
        if (index < membership.length) {
            int[] tmp = new int[index];
            System.arraycopy(nonEmpty, 0, tmp, 0, index);
            return tmp;
        }
        return nonEmpty;
    }


    private XCluster createOtherCluster() {
        int[] unclassified = algorithm.getUnclassified();
        double[] membership = algorithm.getUnclassifiedMembership();
//        System.out.println(ArrayUtils.toString(membership));
        XCluster.Member[] members = new XCluster.Member[unclassified.length];

        for (int i = 0; i < unclassified.length; i++) {
            members[i] = new XCluster.Member(context.getSnippets()[unclassified[i]], membership[i]);
        }

        return new XClusterImpl(new String[]{"Other"}, members);
    }

    /** create Member objects for a cluster, given a cluster membership vector */
    private XCluster.Member[] createMembers(double[] membership) {

        int[] indices = ArrayUtils.getNonZeroIndices(membership);
        XCluster.Member[] members = new XCluster.Member[indices.length];
        for (int i = 0; i < indices.length; i++) {
            int id = indices[i];
            members[i] = new XCluster.Member(context.getSnippets()[id], membership[id]);
        }
        return members;
    }



}
