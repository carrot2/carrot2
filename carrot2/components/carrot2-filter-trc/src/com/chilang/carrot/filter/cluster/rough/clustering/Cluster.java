
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.FeatureVector;
import com.chilang.carrot.filter.cluster.rough.Snippet;

import java.util.Map;

public interface Cluster {

    /**
     * Return cluster size (number of cluster's members)
     */
    public int size();

    /**
     * Return cluster's members
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
     */
    String[] getLabels();

    //TODO remove

    void setLabels(String[] labels);

}
