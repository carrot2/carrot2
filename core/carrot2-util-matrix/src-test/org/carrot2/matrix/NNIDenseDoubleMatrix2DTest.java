
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

import org.carrot2.matrix.NNIDenseDoubleMatrix2D;
import org.carrot2.matrix.NNIDoubleFactory2D;
import org.junit.Test;

import org.apache.mahout.math.matrix.*;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;

/**
 * Test cases for {@link NNIDenseDoubleMatrix2D}.
 */
@SuppressWarnings("deprecation")
public class NNIDenseDoubleMatrix2DTest
{
    /** An example input matrix */
    private DoubleMatrix2D A = DoubleFactory2D.dense.make(new double [] []
    {
        {
            1.00, 0.50, 0.00, 9.00
        },
        {
            2.00, 0.25, 5.00, 3.00
        },
        {
            3.00, 4.00, 3.00, 1.00
        },
        {
            4.00, 1.00, 0.50, 3.00
        },
        {
            5.00, 7.00, 6.00, 4.00
        }
    });

    /** An example input matrix */
    private DoubleMatrix2D C = DoubleFactory2D.dense.make(new double [] []
    {
        {
            1.00, 0.50, 0.00, 9.13, 7.00
        },
        {
            4.00, 1.45, 0.50, 3.00, 3.00
        },
        {
            5.00, 7.00, 6.00, 4.00, 2.13
        }
    });

    /** An example input matrix */
    private DoubleMatrix2D E = DoubleFactory2D.dense.make(new double [] []
    {
        {
            1.00, 2.00, 3.00, 4.00, 5.00
        },
        {
            6.00, 7.00, 8.00, 9.00, 10.00
        },
        {
            11.00, 12.00, 13.00, 14.00, 15.00
        }
    });

    @Test
    public void testZMultLevel3()
    {
        DoubleMatrix2D nniA = new NNIDenseDoubleMatrix2D(A.toArray());
        DoubleMatrix2D nniC = new NNIDenseDoubleMatrix2D(C.toArray());
        DoubleMatrix2D nniB = new NNIDenseDoubleMatrix2D(A.viewDice().toArray());
        DoubleMatrix2D coltB = new DenseDoubleMatrix2D(A.viewDice().toArray());
        DoubleMatrix2D D = NNIDoubleFactory2D.nni.random(3, 4);

        assertThat(nniB.zMult(nniA, null)).isEquivalentTo(coltB.zMult(A, null));
        assertThat(nniB.zMult(nniC, null, 1, 0, false, true)).isEquivalentTo(
            coltB.zMult(nniC, null, 1, 0, false, true));
        assertThat(nniB.zMult(D, null, 1, 0, true, true)).isEquivalentTo(
            coltB.zMult(D, null, 1, 0, true, true));
    }

    @Test
    public void testZMultLevel3WithDiceViews()
    {
        DoubleMatrix2D nniB = new NNIDenseDoubleMatrix2D(A.viewDice().toArray());
        DoubleMatrix2D coltB = new DenseDoubleMatrix2D(A.viewDice().toArray());
        DoubleMatrix2D D = DoubleFactory2D.dense.random(4, 3);

        assertThat(nniB.zMult(D.viewDice(), null, 1, 0, true, true)).isEquivalentTo(
            coltB.zMult(D.viewDice(), null, 1, 0, true, true));
    }

    @Test
    public void testColtSelectionViewBugWorkaround()
    {
        DoubleMatrix2D nniB = NNIDoubleFactory2D.nni.random(5, 4);
        DoubleMatrix2D nniD = NNIDoubleFactory2D.nni.random(3, 4);
        int [] rows = new int []
        {
            0, 2
        };

        DoubleMatrix2D nni = nniB.zMult(nniD.viewSelection(rows, null), null, 1, 0,
            false, true);

        DoubleMatrix2D colt = nniD.viewSelection(rows, null).viewDice().zMult(
            nniB.viewDice(), null, 1, 0, true, false).viewDice();

        assertThat(nni).isEquivalentTo(colt);
    }

    @Test
    public void testTranspose()
    {
        NNIDenseDoubleMatrix2D copyE = new NNIDenseDoubleMatrix2D(E.toArray());
        copyE.transpose();
        assertThat(copyE).isEquivalentTo(E.viewDice());
    }

    @Test
    public void testTranspose2x2Matrix()
    {
        NNIDenseDoubleMatrix2D matrix = new NNIDenseDoubleMatrix2D(new double [] []
        {
            new double []
            {
                1, 2
            }, new double []
            {
                3, 4
            }
        });
        final NNIDenseDoubleMatrix2D copy = (NNIDenseDoubleMatrix2D) matrix.clone();
        copy.transpose();
        assertThat(matrix).isEquivalentTo(copy.viewDice());
    }
}
