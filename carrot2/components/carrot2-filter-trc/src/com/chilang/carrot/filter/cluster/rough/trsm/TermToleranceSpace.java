/**
 * 
 * @author chilang
 * Created 2003-08-21, 15:54:19.
 */
package com.chilang.carrot.filter.cluster.rough.trsm;

import cern.colt.bitvector.BitVector;
/**
 * Represent tolerance space of terms in which
 * each term can be approximated by a tolerance class of "similar" terms.
 */
public class TermToleranceSpace implements ToleranceSpace{

    /**
     * Number of terms
     */
    int size;
    /**
     * Matrix of term * term co-occurences;
     * cell (i,j) - number of document in which term i and j co-occur
     */
    int[][] termCooccurences;
    /**
     * vector[i] is a bit vector that represent tolerance class of term i;
     * bit j of vector[i] is set if j is in tolerance class of j
     * (j co-occur with i over a given threshold)
     */
    BitVector[] toleranceClasses;
    /**
     * Contruct term co-occurences matrix wrapper based on
     * term * document frequency matrix
     * @param td term * document frequency matrix (row = doc, term = column)
     * @param threshold co-occurence threshold over or equals
     * which a term is treated as belonging to the same tolerance class
     */

        
    public TermToleranceSpace(int[][] td, int threshold) {

        termCooccurences = constructCooccurencesMatrix(td);
        toleranceClasses = constructToleranceMatrix(termCooccurences, threshold);

    }

    private int[][] constructCooccurencesMatrix(int[][] td) {
        int nterms = td[0].length;
        int ndocs = td.length;
        int[][] cooc = new int[nterms][nterms];

        //element (i,j) is set if term i and j co-occurs
        //in more than a threshold times
        for (int i=0; i <nterms; i++) {
            for (int j=0; j<nterms; j++) {
                //count number of document in which terms co-occurs
                int count = 0;
                for (int k=0; k<ndocs; k++) {
                    //check if term i,j co-occurs in document k
                    if (td[k][i] > 0 && td[k][j] > 0)
                        count++;
                }
                cooc[i][j] = count;
            }
        }
        return cooc;
    }

    /**
     * Construct array of tolerance classes for terms
     * @param cooccurencesMatrix term co-occurences matrix
     * @param threshold threshold of co-occurences over or equal which two term are consider similar
     * (belongs to each other's tolerance class)
     * @return array of bit vector representing tolerance classes for terms
     */
    private BitVector[] constructToleranceMatrix(int[][] cooccurencesMatrix, int threshold)  {
        size = cooccurencesMatrix.length;
        BitVector[] tm = new BitVector[size];
        for (int i = 0; i < size; i++) {
            tm[i] = new BitVector(size);
            for (int j = 0; j < size; j++) {
                //set bit i,j if co-occurences is over or equal given threshold
                tm[i].putQuick(j, cooccurencesMatrix[i][j] >= threshold);
            }
            //term always belongs to its tolenrance class
            tm[i].putQuick(i, true);
        }
        return tm;
    }

    /**
     * Return tolerance class for specified term.
     *
     * @param id id of term
     * @return BitVector representing term's tolerance class.
     * Bit i of vector is set if term i is in tolerance class.
     * <b>ATTENTION</b> This vector is backed by internal representation and should not
     * be modified (must be copied first if modification is necessary)
     */
    public Object getToleranceClass(int id) {
        //create copy of matrix row id
        //  so that modification doesn't corrupts internal representation
        return toleranceClasses[id];
    }

    public Object[] getToleranceClasses() {
        return toleranceClasses;
    }

    public int[][] getTermCooccurences() {
        return termCooccurences;
    }

    public int getSize() {
        return size;
    }

    public int[][] getToleranceMatrix() {
        int[][] binary = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (toleranceClasses[i].getQuick(j))
                   binary[i][j] = 1;
            }
        }
        return binary;
    }
}
