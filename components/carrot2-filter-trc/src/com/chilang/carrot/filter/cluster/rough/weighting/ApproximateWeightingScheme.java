/**
 * 
 * @author chilang
 * Created 2003-12-15, 00:31:55.
 */
package com.chilang.carrot.filter.cluster.rough.weighting;


/**
 * Weighting scheme with support for upper approximation
 */
public interface ApproximateWeightingScheme {


    /**
     * Get weight matrix for upper approximation of documents
     * @return
     */
    public double[][] getUpperWeight();
}
