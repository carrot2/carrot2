
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

import com.chilang.carrot.filter.cluster.rough.FeatureVector;

public class ClusterRepresentative implements Clusterable {



    protected int id = 0;

    protected FeatureVector features;

    public int getIdentifier() {
        return id;
    }

    public String toString() {
        return features.toString();
    }

    public FeatureVector getFeatures() {
        return features;
    }

    public ClusterRepresentative(FeatureVector featureVector) {
        this.features = featureVector;
    }
    public ClusterRepresentative(int id, FeatureVector featureVector) {
        this.id = id;
        this.features = featureVector;
    }

    public void setFeatures(FeatureVector featureVector) {
        features = featureVector;
    }
}
