/*
 * NNIDenseDoubleMatrix2DTest.java Created on 2004-06-11
 */
package cern.colt.matrix.impl;

import cern.colt.matrix.*;
import junit.framework.*;

/**
 * @author stachoo
 */
public class NNIDenseDoubleMatrix2DTest extends TestCase
{

    /** An example input matrix */
    private DoubleMatrix2D A = DoubleFactory2D.dense.make(new double[][]
    {
    { 1.00, 0.50, 0.00, 9.00},
    { 2.00, 0.25, 5.00, 3.00},
    { 3.00, 4.00, 3.00, 1.00},
    { 4.00, 1.00, 0.50, 3.00},
    { 5.00, 7.00, 6.00, 4.00}});

    /** An example input matrix */
    private DoubleMatrix2D C = DoubleFactory2D.dense.make(new double[][]
    {
    { 1.00, 0.50, 0.00, 9.13, 7.00},
    { 4.00, 1.45, 0.50, 3.00, 3.00},
    { 5.00, 7.00, 6.00, 4.00, 2.13}});

    /** An example input matrix */
    private DoubleMatrix2D E = DoubleFactory2D.dense.make(new double[][]
    {
    { 1.00, 2.00, 3.00, 4.00, 5.00},
    { 6.00, 7.00, 8.00, 9.00,10.00},
    {11.00,12.00,13.00,14.00,15.00}});

    /**
     * 
     */
    public void testZMultLevel3()
    {
        DoubleMatrix2D nniB = new NNIDenseDoubleMatrix2D(A.viewDice().toArray());
        DoubleMatrix2D coltB = new DenseDoubleMatrix2D(A.viewDice().toArray());
        DoubleMatrix2D D = DoubleFactory2D.dense.random(3, 4);
        
        assertEquals("Equal results", coltB.zMult(A, null), nniB.zMult(A, null));
        assertEquals("Equal results", coltB.zMult(C, null, 1, 0, false, true),
                nniB.zMult(C, null, 1, 0, false, true));
        assertEquals("Equal results", coltB.zMult(D, null, 1, 0, true, true),
                nniB.zMult(D, null, 1, 0, true, true));
    }
    
    public void testDeepTranspose()
    {
        DenseDoubleMatrix2D copyE = new NNIDenseDoubleMatrix2D(E.toArray());
        NNIDenseDoubleMatrix2D.deepTranspose(copyE);
        assertEquals("Transpose: equal results", E.viewDice(), copyE);
    }
}