/**
 * 
 * @author chilang
 * Created 2003-12-14, 21:38:37.
 */
package com.chilang.carrot.filter.cluster.rough.trsm;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;


/**
 * Upper approximation of Document-Term weighted matrix.
 * Approximation is constructed for given threshold :
 *
 * U(doc) = { term | Inclusion(ToleranceSet(term), doc) > threshold}
 *
 * Special weighting for terms contained in document's upper appoximation
 * is applied.
 */
public class DocumentApproximationSpace {

    //document-term upper approximation matrix
    DoubleMatrix2D upperApproximation;

    DoubleMatrix2D documentTerm;

    double inclusionThreshold;

    public DocumentApproximationSpace(double upperThreshold, double[][] dtMatrix) {
        inclusionThreshold = upperThreshold;
        documentTerm = DoubleFactory2D.sparse.make(dtMatrix);

    }

    

    /**
     * Get IDF factor (inverse document frequency) for given term
     * @param i term id
     * @return
     */
    protected double getIDF(int i) {
        return 0;
    }
    public double[] getUpperApproximationById(int id) {
        return upperApproximation.viewRow(id).toArray();
    }

}
