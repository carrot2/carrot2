/*
 * MatrixUtilsTest.java Created on 2004-05-13
 */
package com.stachoodev.matrix;

import cern.colt.matrix.*;
import junit.framework.*;

/**
 * Unit tests for the MatrixUtils class.
 * 
 * @author stachoo
 */
public class MatrixUtilsTest extends TestCase
{
    /**
     *  
     */
    public void testComputeOrthogonality()
    {
        /** An orthogonal matrix */
        DoubleMatrix2D orthogonal = DoubleFactory2D.dense.make(new double [] []
        {
        { 1.00, 0.00, 0.00, 0.00, 0.00 },
        { 0.00, 1.00, 0.00, 0.00, 0.00 },
        { 0.00, 0.00, 1.00, 0.00, 0.00 },
        { 0.00, 0.00, 0.00, 1.00, 0.00 },
        { 0.00, 0.00, 0.00, 0.00, 1.00 } });

        assertEquals("Orthogonal matrix", 0, MatrixUtils
            .computeOrthogonality(orthogonal), 0);

        /** A matrix with identical columns */
        DoubleMatrix2D identical = DoubleFactory2D.dense.make(new double [] []
        {
        { 0.00, 0.00, 0.00 },
        { 0.49, 0.49, 0.49 },
        { 0.49, 0.49, 0.49 },
        { 0.72, 0.72, 0.72 },
        { 0.00, 0.00, 0.00 } });

        assertEquals("Identical matrix", 1, MatrixUtils
            .computeOrthogonality(identical), 0.02);
    }

    /**
     * 
     */
    public void testNormaliseColumnsEL()
    {
        /** A matrix with unnormalised columns */
        DoubleMatrix2D unnormalisedColumns = DoubleFactory2D.dense
            .make(new double [] []
            {
            { -1.00, 0.00, 0.00, -7.00 },
            { 0.00, 2.00, 0.50, 19.00 },
            { 0.00, 5.00, 5.00, 10.00 } });
        MatrixUtils.normaliseColumnL2(unnormalisedColumns, null);

        for (int c = 0; c < unnormalisedColumns.columns(); c++)
        {
            double length = 0;
            for (int r = 0; r < unnormalisedColumns.rows(); r++)
            {
                length += unnormalisedColumns.get(r, c)
                    * unnormalisedColumns.get(r, c);
            }

            // Note: don't need to take a square root of the length as it is
            // supposed to be 1.0 anyway
            assertEquals("Column " + c + "length", 1.0, 1.0, 0.02);
        }
    }
}