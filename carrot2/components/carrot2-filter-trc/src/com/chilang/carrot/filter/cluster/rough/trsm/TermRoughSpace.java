/**
 * 
 * @author chilang
 * Created 2003-08-21, 14:44:48.
 */
package com.chilang.carrot.filter.cluster.rough.trsm;

import cern.colt.bitvector.BitVector;
import com.chilang.carrot.filter.cluster.rough.SparseFeatureVector;
import com.chilang.carrot.filter.cluster.rough.weighting.ApproximateWeightingScheme;
import com.chilang.carrot.filter.cluster.rough.weighting.UpperTfIdfWeightingScheme;
import com.chilang.util.MatrixUtils;

/**
 * Tolerance space for a set of terms.
 * Tolerance relation between term is based on its
 * co-occurences i.e. :
 *   tolerance classes for a given term is formed by terms
 *   that it co-occurs over a threshold times.
 */
public class TermRoughSpace implements RoughSpace{

    protected int toleranceThreshold;

    protected int size; // number of terms

    ToleranceSpace termCooccurencesMatrix;

    ApproximateWeightingScheme weightingScheme;

    BitVector[] documentMatrix;
    /**
     * Upper approximation of documents represented as bit matrix
     */
    BitVector[] upperApproximationMatrix;
    /**
     * Construct term rough space
     * @param documentTermFrequency document * term frequency matrix
     * @param cooccurenceThreshold co-occurence cooccurenceThreshold
     * @param inclusionThreshold
     */
    public TermRoughSpace(int[][] documentTermFrequency, int cooccurenceThreshold, double inclusionThreshold) {
        toleranceThreshold = cooccurenceThreshold;
        size = documentTermFrequency[0].length;

        termCooccurencesMatrix = new TermToleranceSpace(documentTermFrequency, toleranceThreshold);

        //construct document-term bit matrix
        documentMatrix = MatrixUtils.convert(documentTermFrequency, new MatrixUtils.IntThresholdFilter(0));

        //construct inclusion matrix
        double[][] inclusion = constructInclusionMatrix(documentMatrix, (BitVector[])termCooccurencesMatrix.getToleranceClasses());

        //inclusion over given threshold is treated as belonging in upper approximation
        upperApproximationMatrix =
                MatrixUtils.convert(inclusion, new MatrixUtils.DoubleThresholdFilter(inclusionThreshold));

        weightingScheme = new UpperTfIdfWeightingScheme(documentTermFrequency, upperApproximationMatrix);
    }

    public BitVector getToleranceClass(int id) {
        return (BitVector)termCooccurencesMatrix.getToleranceClass(id);
    }

    /**
     * Calculate lower approximation for given feature set
     * @param featureSet
     * @return feature set (represented as bit vector)
     * that is a lower approximation of specified feature set
     */
    public BitVector lowerApproximation(BitVector featureSet) {
        //array of intersection of tolerance classes and given feature set

        BitVector lower = new BitVector(size);

        for (int i=0; i<size;i++) {
            double inclusion = inclusionDegree(i, featureSet);

            lower.put(i, inclusion == 1.0);
        }
        return lower;
    }

    /**
     * Calculate upper approximation for given feature set
     * @param featureSet set of term (represented in bit vector)
     * @return feature set (represented as bit vector)
     * that is a upper approximation of specified feature set
     */
    public BitVector upperApproximation(BitVector featureSet) {
        //array of intersection of tolerance classes and given feature set

        BitVector upper = new BitVector(size);

        for (int i=0; i<size;i++) {
            double inclusion = inclusionDegree(i, featureSet);

            upper.put(i, inclusion > 0.0);
        }
        return upper;
    }

    /**
     * Calculate inclusion degree between given feature set and tolerance class of term i
     * (i.e. how much of feature set is included in tolerance class of term i)
     * @param featureSet set of terms (represented in bit vector)
     * @param i id of term which tolerance class is to be compared with
     * @return degree of inclusion
     */
    private double inclusionDegree(int i, BitVector featureSet) {
        BitVector intersection = featureSet.copy();
        //calculate intersection between two set :
        // B = feature set and
        // A = tolerance class of term i
        BitVector toleranceClass = (BitVector)termCooccurencesMatrix.getToleranceClass(i);
        intersection.and(toleranceClass);

        //calculate degree of inclusion between A, B as | A ^ B | / | A |
        // i.e. how much is tolerance class of term i is included in given feature set
        double inclusion = (double)intersection.cardinality() / (double)toleranceClass.cardinality();
        return inclusion;
    }

    public Object lowerApproximation(Object x) {
        return lowerApproximation((BitVector)x);
    }

    public Object upperApproximation(Object x) {
        return upperApproximation((BitVector)x);
    }

    /**
     * Return weighted upper approximation of specified object
     * @param id
     * @return
     */
    public Object getWeightedUpperApproximation(int id) {
       return new SparseFeatureVector(weightingScheme.getUpperWeight()[id]);
    }

    /**
     * Construct inclusion matrix for set of documents (given as array of document bit vector)
     * and set of tolerance classes of term (given as array of bit vectors).
     *
     * The cell [i,j] of resulting matrix contains real value representing
     * inclusing degree of tolerance class of term j in document i
     * (i.e. how much of tolerance class of term j is includes in document i)
     *
     * The inclusion matrix is constructed as follow :
     *   matrix[i,j] = (tf[i] AND tc[j]) / tc[j].card
     * where
     *   tf[i] = i-th document vector (bit set = term occurs in document)
     *   tc[j] = j-th bit vector representing tolerance class of term j (bit set = term in tolerance class);
     *   tc[j].card = cardinality of bit vector = number of bits set
     *   AND = binary AND operation on two bit vectors
     *
     * Arguments are all unchanged.
     * @param doc array of m bit vectors (of lenght n) representing documents
     * doc[i] = document i; bit set = term occurs in document
     * @param tc array of n bit vector (representing n*n bit matrix )
     * tc[i] = bit vector of length n representing tolerance class of term i :
     *   bit j is set if term j belongs to tolerance class of term i
     *   (term i and j belongs to each other tolerance class)
     * @return inclusion matrix of size m * n (m documents, n terms = n tolerance classes)
     */
    private double[][] constructInclusionMatrix(BitVector[] doc, BitVector[] tc) {
        int rows = doc.length, cols = tc.length;
        double[][] inclusion = new double[rows][cols];
        for(int j = cols; --j >= 0; ) {
            int card = tc[j].cardinality();
            for(int i = rows; --i >= 0; ) {
                BitVector b = tc[j].copy();
                b.and(doc[i]);
                inclusion[i][j] = (double)b.cardinality()/card;
            }
        }
        return inclusion;
    }


    public double[][] getUpperWeight() {
        return weightingScheme.getUpperWeight();
    }

    public BitVector[] getDocumentMatrix() {
        return documentMatrix;
    }

    public BitVector[] getUpperApproximationMatrix() {
        return upperApproximationMatrix;
    }

    public ToleranceSpace getToleranceSpace() {
        return termCooccurencesMatrix;
    }

}
