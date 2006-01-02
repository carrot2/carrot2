package com.chilang.carrot.filter.cluster.rough.measure;

import com.chilang.carrot.filter.cluster.rough.clustering.Clusterable;

/**
 * Interface for similarity measure.
 */
public interface Similarity {
    public double measure(Clusterable obj1, Clusterable obj2);
    public double measure(double[] vector1, double[] vector2);
}
