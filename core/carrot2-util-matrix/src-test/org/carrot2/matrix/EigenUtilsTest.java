
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import static org.carrot2.matrix.MatrixAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import org.apache.mahout.math.matrix.DoubleMatrix1D;
import org.apache.mahout.math.matrix.*;

/**
 * Test cases for {@link EigenUtils}.
 */
@SuppressWarnings("deprecation")
public class EigenUtilsTest
{
    @Test
    public void testEmpty()
    {
        assertThat(
            EigenUtils.principalEigenvector(NNIDoubleFactory2D.nni.make(0, 0)).size())
            .isEqualTo(0);
    }

    @Test
    public void testSimpleExample()
    {
        final DoubleMatrix2D A = NNIDoubleFactory2D.nni.make(new double [] []
        {
            {
                0, 0, 1, 0
            },
            {
                1, 0, 0, 0
            },
            {
                1, 1, 0, 1
            },
            {
                0, 0, 0, 0
            },
        });

        final DoubleMatrix1D ranks = EigenUtils.principalEigenvector(A);
        assertThat(ranks).isEquivalentTo(new double []
        {
            0.372491, 0.195901, 0.394107, 0.0375
        }, 0.001);
    }
}
