/**
 * 
 * @author chilang
 * Created 2003-07-24, 12:23:07.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.FeatureVector;

public class ClusterRepresentative implements Clusterable {



    protected int id = 0;

    protected FeatureVector features;

    public int getIdentifier() {
        return id;  //To change body of implemented methods use Options | File Templates.
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
