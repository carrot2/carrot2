/**
 * 
 * @author chilang
 * Created 2003-12-30, 01:04:12.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.data.IRContext;


/**
 * Clusterer can cluster collection of objects
 * and clustering results can be collected.
 * Provide simple interface that wrap clustering algorithm and its context data
 */
public interface Clusterer {



    /**
     * Run clustering
     */
    public void cluster();


    /**
     * Return resulting clusters
     * @return
     */
    public XCluster[] getClusters();

    void setContext(IRContext context);
}
