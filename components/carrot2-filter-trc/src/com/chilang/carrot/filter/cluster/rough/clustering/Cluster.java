/**
 * 
 * @author chilang
 * Created 2003-07-23, 16:15:45.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.FeatureVector;
import com.chilang.carrot.filter.cluster.rough.Snippet;

import java.util.Map;

public interface Cluster {

    /**
     * Return cluster size (number of cluster's members)
     * @return
     */
    public int size();

    /**
     * Return cluster's members
     * @return
     */
    public Snippet[] getMembers();

    //TODO remove

    public void setMembers(Snippet[] objs);

    public Clusterable getRepresentative();



    /**
     * Return representative term -> weight (how significant a term is in this cluster)  map.
     *
     * @return a map of (Term -> Double)
     */
    public Map getRepresentativeTerm();


    //TODO remove

    public void setRepresentativeTerm(Map termWeightMap);

    //TODO refactor

    public FeatureVector getMembership();

    public void setMembership(FeatureVector featureVector);

    /**
     * Return description labels for cluster
     * @return
     */
    String[] getLabels();

    //TODO remove

    void setLabels(String[] labels);

}
