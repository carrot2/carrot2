/**
 * 
 * @author chilang
 * Created 2003-07-24, 12:13:19.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.FeatureVector;

/**
 * Represent object that can be clustered
 */
public interface Clusterable {
    public int getIdentifier();

    public FeatureVector getFeatures();

    public void setFeatures(FeatureVector featureVector);

}
