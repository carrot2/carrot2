/**
 *
 * @author chilang
 * Created 2003-08-19, 17:58:33.
 */
package com.chilang.carrot.filter.cluster.rough.weighting;

/**
 * Calculate document * term weight matrix
 */
public interface WeightingScheme {


    /**
     * Return document * term weight matrix
     * @return
     */
    public double[][] getTermWeight();
}
