
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.util.tests.CarrotTestCase;
import org.fest.assertions.Delta;
import org.junit.Test;

import com.carrotsearch.hppc.sorting.IndirectComparator;

/**
 * Test cases for {@link MatrixUtils}.
 */
public class MatrixUtilsTest extends CarrotTestCase
{
    @Test
    public void testComputeOrthogonalityOrthogonal()
    {
        /** An orthogonal matrix */
        DoubleMatrix2D orthogonal = new DenseDoubleMatrix2D(new double [] []
        {
            {
                1.00, 0.00, 0.00, 0.00, 0.00
            },
            {
                0.00, 1.00, 0.00, 0.00, 0.00
            },
            {
                0.00, 0.00, 1.00, 0.00, 0.00
            },
            {
                0.00, 0.00, 0.00, 1.00, 0.00
            },
            {
                0.00, 0.00, 0.00, 0.00, 1.00
            }
        });

        assertThat(MatrixUtils.computeOrthogonality(orthogonal)).isEqualTo(0.0,
            Delta.delta(0.00));
    }

    @Test
    public void testComputeOrthogonalityIdenticalColumns()
    {
        /** A matrix with identical columns */
        DoubleMatrix2D identical = new DenseDoubleMatrix2D(new double [] []
        {
            {
                0.00, 0.00, 0.00
            },
            {
                0.49, 0.49, 0.49
            },
            {
                0.49, 0.49, 0.49
            },
            {
                0.72, 0.72, 0.72
            },
            {
                0.00, 0.00, 0.00
            }
        });

        assertThat(MatrixUtils.computeOrthogonality(identical)).isEqualTo(1.0,
            Delta.delta(0.02));
    }

    @Test
    public void testNormalizeColumnsL1()
    {
        /** A matrix with unnormalized columns */
        DoubleMatrix2D unnormalisedColumns = nonZeroColumnMatrix();
        MatrixUtils.normalizeColumnL1(unnormalisedColumns, new double [1]);

        checkL1Normalization(unnormalisedColumns);
    }

    @Test
    public void testNormalizeColumnsL1Zeros()
    {
        /** A matrix with unnormalized columns */
        DoubleMatrix2D unnormalisedColumns = zeroColumnMatrix();
        MatrixUtils.normalizeColumnL1(unnormalisedColumns, new double [1]);

        checkL1Normalization(unnormalisedColumns);
    }

    @Test
    public void testNormalizeColumnsL2()
    {
        /** A matrix with unnormalized columns */
        DoubleMatrix2D unnormalisedColumns = nonZeroColumnMatrix();
        MatrixUtils.normalizeColumnL2(unnormalisedColumns, null);

        checkL2Normalization(unnormalisedColumns);
    }

    @Test
    public void testNormalizeColumnsL2Zeros()
    {
        /** A matrix with unnormalized columns */
        DoubleMatrix2D unnormalisedColumns = zeroColumnMatrix();
        MatrixUtils.normalizeColumnL2(unnormalisedColumns, null);

        checkL2Normalization(unnormalisedColumns);
    }

    @Test
    public void testSparseNormalizeColumnsL2()
    {
        /** A matrix with unnormalized columns */
        DoubleMatrix2D unnormalisedColumns = nonZeroColumnMatrix();
        MatrixUtils.normalizeSparseColumnL2(unnormalisedColumns, null);

        checkL2Normalization(unnormalisedColumns);
    }

    @Test
    public void testSparseNormalizeColumnsL2Zeros()
    {
        /** A matrix with unnormalized columns */
        DoubleMatrix2D unnormalisedColumns = zeroColumnMatrix();
        MatrixUtils.normalizeSparseColumnL2(unnormalisedColumns, null);

        checkL2Normalization(unnormalisedColumns);
    }

    @Test
    public void testSumRows()
    {
        assertThat(MatrixUtils.sumRows(nonZeroColumnMatrix(), null)).isEqualTo(
            new double []
            {
                11, 21.5, 3
            });
    }

    @Test
    public void testMaxInRow()
    {
        assertThat(MatrixUtils.maxInRow(nonZeroColumnMatrix(), 1)).isEqualTo(3);
    }

    @Test
    public void testMinInColumns()
    {
        final double [] expectedMinValues = new double []
        {
            -1.00, 0, 0.50, -7.00
        };

        final int [] expectedMinIndices = new int []
        {
            0, 0, 1, 2
        };

        final double [] actualMinValues = new double [4];
        final int [] actualMinColumnIndices = MatrixUtils.minInColumns(
            nonZeroColumnMatrix(), null, actualMinValues);

        assertThat(actualMinColumnIndices).isEqualTo(expectedMinIndices);
        assertThat(actualMinValues).isEqualTo(expectedMinValues);
    }

    @Test
    public void testMaxInColumns()
    {
        final double [] expectedMaxValues = new double []
        {
            0.00, 5.00, 5.00, 19.00
        };

        final int [] expectedMaxIndices = new int []
        {
            1, 2, 2, 1
        };

        final double [] actualMaxValues = new double [4];
        final int [] actualMaxColumnIndices = MatrixUtils.maxInColumns(
            nonZeroColumnMatrix(), null, actualMaxValues);

        assertThat(actualMaxColumnIndices).isEqualTo(expectedMaxIndices);
        assertThat(actualMaxValues).isEqualTo(expectedMaxValues);
    }

    @Test
    public void testMinSparseness()
    {
        final DoubleMatrix2D sparse = new DenseDoubleMatrix2D(2, 2);
        assertThat(MatrixUtils.computeSparseness(sparse)).isEqualTo(0);
    }

    @Test
    public void testMaxSparseness()
    {
        final DoubleMatrix2D sparse = new DenseDoubleMatrix2D(2, 2);
        sparse.assign(3);
        assertThat(MatrixUtils.computeSparseness(sparse)).isEqualTo(1);
    }

    @Test
    public void frobeniusNorm()
    {
        assertThat(MatrixUtils.frobeniusNorm(new DenseDoubleMatrix2D(new double [] []
        {
            {
                1, -1
            },
            {
                2, 0
            }
        }))).isEqualTo(Math.sqrt(1 * 1 + (-1) * (-1) + 2 * 2));
    }

    @Test
    public void sortedRowsView()
    {
        final DenseDoubleMatrix2D input = new DenseDoubleMatrix2D(new double [] []
        {
            {
                1, -1
            },
            {
                2, -2
            },
            {
                3, -3
            }
        });

        final int [] order = new int []
        {
            2, 1, 0
        };

        MatrixAssertions.assertThat(
            MatrixUtils.sortedRowsView(input,
                new IndirectComparator.AscendingIntComparator(order))).isEquivalentTo(
            new double [] []
            {
                {
                    3, -3
                },
                {
                    2, -2
                },
                {
                    1, -1
                }
            });
    }

    /**
     *
     */
    private DoubleMatrix2D nonZeroColumnMatrix()
    {
        return new DenseDoubleMatrix2D(new double [] []
        {
            {
                -1.00, 0.00, 2.00, 10.00
            },
            {
                0.00, 2.00, 0.50, 19.00
            },
            {
                0.00, 5.00, 5.00, -7.00
            }
        });
    }

    /**
     *
     */
    private DoubleMatrix2D zeroColumnMatrix()
    {
        return new DenseDoubleMatrix2D(new double [] []
        {
            {
                0.00, 0.00, 0.00, -7.00
            },
            {
                0.00, 2.00, 0.50, 19.00
            },
            {
                0.00, 5.00, 5.00, 10.00
            }
        });
    }

    private void checkL1Normalization(DoubleMatrix2D unnormalisedColumns)
    {
        for (int c = 0; c < unnormalisedColumns.columns(); c++)
        {
            double length = 0;
            for (int r = 0; r < unnormalisedColumns.rows(); r++)
            {
                length += unnormalisedColumns.get(r, c);
            }

            // Ignore all-zero columns
            if (length != 0)
            {
                assertThat(length).as("Column " + c + "length").isEqualTo(1.0,
                    Delta.delta(0.02));
            }
        }
    }

    private void checkL2Normalization(DoubleMatrix2D unnormalisedColumns)
    {
        for (int c = 0; c < unnormalisedColumns.columns(); c++)
        {
            double length = 0;
            for (int r = 0; r < unnormalisedColumns.rows(); r++)
            {
                length += unnormalisedColumns.get(r, c) * unnormalisedColumns.get(r, c);
            }

            // Note: don't need to take a square root of the length as it is
            // supposed to be 1.0 anyway
            // Ignore all-zero columns
            if (length != 0)
            {
                assertThat(length).as("Column " + c + "length").isEqualTo(1.0,
                    Delta.delta(0.02));
            }
        }
    }
}
