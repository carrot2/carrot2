package com.chilang.carrot.filter.cluster.rough.clustering;


/**
 * Label generator for a set of clusters
 */
public interface ClusterLabelGenerator {


    /**
     * Generate label for a cluster identified by an id
     * @param id
     * @return
     */
    public String[] getLabel(int id);
}
