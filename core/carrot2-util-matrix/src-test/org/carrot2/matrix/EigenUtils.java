
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

import org.apache.mahout.math.matrix.DoubleMatrix1D;
import org.apache.mahout.math.matrix.*;
import org.apache.mahout.math.function.Functions;

/**
 *
 */
@SuppressWarnings("deprecation")
class EigenUtils
{
    public static final double DEFAULT_EPSILON = 0.001;

    public static final double DEFAULT_DAMP = 0.85;
    
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    public static DoubleMatrix1D principalEigenvector(DoubleMatrix2D A)
    {
        final DoubleMatrix1D E = A.like1D(A.rows());
        final double sourceRankL1 = 1;
        E.assign(sourceRankL1 / E.size());

        return principalEigenvector(A, E);
    }

    public static DoubleMatrix1D principalEigenvector(DoubleMatrix2D A, DoubleMatrix1D E)
    {
        return principalEigenvector(A, E, DEFAULT_EPSILON);
    }

    public static DoubleMatrix1D principalEigenvector(DoubleMatrix2D A, DoubleMatrix1D E,
        double epsilon)
    {
        MatrixUtils.normalizeColumnL1(A, null);
        final DoubleMatrix1D Ri = A.like1D(A.rows());

        // R0 = E
        Ri.assign(E);

        final DoubleMatrix1D Ri1 = Ri.like();

        final double d = DEFAULT_DAMP;
        double delta;
        int iteration = 0;
        do
        {
            // Ri+1 = d x A x Ri + (1-d) x E;
            Ri1.assign(E);
            A.zMult(Ri, Ri1, d, 1-d, false);

            // delta = ||Ri+1 - Ri||1
            delta = Ri1.aggregate(Ri, Functions.plusAbs, Functions.minus);
            Ri.assign(Ri1);
            iteration++;
        }
        while (iteration < DEFAULT_MAX_ITERATIONS && delta > epsilon);
        
        return Ri;
    }
}
