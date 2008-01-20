
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough;

import cern.colt.bitvector.BitVector;
import cern.colt.function.DoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;


public class SparseFeatureVector implements FeatureVector{

    protected DoubleMatrix1D vector; //vector of values

    protected BitVector bitVector;  //store non-zero values bit

    public SparseFeatureVector(double[] values) {
        vector = new SparseDoubleMatrix1D(values);
        bitVector = new BitVector(vector.size());
        for (int i=0; i < vector.size(); i++) {
            bitVector.putQuick(i, values[i] > 0);
        }
    }

    public SparseFeatureVector(int size) {
        vector = new SparseDoubleMatrix1D(size);
        bitVector = new BitVector(size);
    }

    public SparseFeatureVector(BitVector bv, double[] values) {
        vector = new SparseDoubleMatrix1D(values);
        bitVector = bv;
    }

    private SparseFeatureVector(DoubleMatrix1D matrix) {
        vector = matrix;
    }
    public double getWeight(int index) {
        return vector.getQuick(index);
    }

    public void setWeight(int index, double value) {
        vector.setQuick(index, value);
        bitVector.putQuick(index, value > 0);
    }

    public int size() {
        return vector.size();
    }

    public FeatureVector copy() {
        return new SparseFeatureVector(vector.copy());
    }

    /**
     * Return internal representation as bit vector. Non-zero weight bit are set.
     *
     * @return Internal bit vector.
     * ATTENTION : Must be copied before modification (as it will affect this internal representation).
     */
    public BitVector asBitVector() {
        return bitVector;
    }

    public void clear() {
        vector.assign(new DoubleFunction() {
            public double apply(double v) {
                return 0;
            }
        });
        bitVector.clear();
    }

    public int[] getNonZeroIndices() {
        IntArrayList nz = new IntArrayList();
        vector.getNonZeros(nz, null);
        nz.trimToSize();
        return nz.elements();
    }

    public void normalize() {
        IntArrayList nzi = new IntArrayList();
        DoubleArrayList nzv = new DoubleArrayList();
        vector.getNonZeros(nzi, nzv);
        double aggregatedLength = 0;
        int size = nzv.size();
        for (int i = 0; i < size; i++) {
            double w = nzv.getQuick(i);
            aggregatedLength += w * w;
        }
        aggregatedLength = Math.sqrt(aggregatedLength);
        for (int i = 0; i < size; i++) {
            vector.setQuick(nzi.getQuick(i), nzv.getQuick(i) / aggregatedLength);
        }        
    }


    public String toString() {
        return vector.toString();
    }
}
