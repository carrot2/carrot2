/**
 * Created by IntelliJ IDEA.
 * User: chilang
 * Date: 2003-07-17
 * Time: 03:02:32
 * To change this template use Options | File Templates.
 */
package com.chilang.carrot.filter.cluster.rough.trsm;



/**
 * Tolerance space for a closed set of object
 */
public interface ToleranceSpace {
    /**
     * Return tolerance class for specified object
     * @param id object's id
     * @return set of objects forming tolerance class
     */
    public Object getToleranceClass(int id);


    /**
     * Return all tolerance classes in this tolerance space
     * @return array of objects representing tolerance classes
     */
    public Object[] getToleranceClasses();

    /**
     * Return binary (n * n) matrix defining tolerance classes.
     * Cell[i,j] = 1 iff object i and j are in the same tolerance classes
     * @return
     */
//    public int[][] getToleranceMatrix();

}
