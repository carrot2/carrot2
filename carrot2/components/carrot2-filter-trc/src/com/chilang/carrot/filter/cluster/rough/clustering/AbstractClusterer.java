/**
 * 
 * @author chilang
 * Created 2003-07-23, 15:55:04.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;



public abstract class AbstractClusterer {

    protected Object[] objects;
    protected Cluster[] clusters;



    public final void doClustering(Object[] objs) {
        this.objects = objs;
        clusters = initialization(objects);
//        System.out.println("CLUS="+clus[0]);
        do {
            clusters = clustering(clusters, objects);
        } while (!stopCondition());

        clusters = postProcessing(clusters, objects);

    }

    protected abstract Cluster[] initialization(Object[] objects);

    protected abstract Cluster[] postProcessing(Cluster[] clusters, Object[] objects);

    protected abstract Cluster[] clustering(Cluster[] clusters, Object[] objects);

    protected abstract boolean stopCondition();


    public Cluster[] getClusters() {
        return clusters;
    }
}
