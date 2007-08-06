
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.trsm;

import cern.colt.bitvector.BitVector;

public interface RoughSpace {

    /**
     * Calculate lower approximation of specified object
     * @param x
     * @return lower approximation of object
     */
    public Object lowerApproximation(Object x);

    /**
     * Calculate upper approximation of specified object
     * @param x
     * @return upper approximation of object
     */
    public Object upperApproximation(Object x);


    /**
     * Return tolerance class of an object specified by
     * @param id id of object
     * @return BitVector representing tolerance class of specified object
     */
    BitVector getToleranceClass(int id);


    /**
     * Get weighted upper approximation of specified object
     * @param id object's id
     * @return weighted approximation of specified object
     */
    public Object getWeightedUpperApproximation(int id);

    double[][] getUpperWeight();

    BitVector[] getDocumentMatrix();

    BitVector[] getUpperApproximationMatrix();

    ToleranceSpace getToleranceSpace();
}
