
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

package org.carrot2.matrix.factorization;

import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;

/**
 * Performs matrix factorization using the Non-negative Matrix Factorization algorithm
 * with minimization of Euclidean Distance between A and UV' and multiplicative updating.
 */
public class NonnegativeMatrixFactorizationED extends IterativeMatrixFactorizationBase
{
    /**
     * Creates the NNINonnegativeMatrixFactorizationED object for matrix A. Before
     * accessing results, perform computations by calling the {@link #compute()}method.
     * 
     * @param A matrix to be factorized
     */
    public NonnegativeMatrixFactorizationED(DoubleMatrix2D A)
    {
        super(A);
    }

    public void compute()
    {
        // Prototype Matlab code for the NMF-ED
        //        
        // function [U, V, C] = nmf-ed(A)
        // [m, n] = size(A);
        // k = 2; % the desired number of base vectors
        // maxiter = 50; % the number of iterations
        // eps = 1e-9; % machine epsilon
        //        
        // U = rand(m, k); % initialise U randomly
        // V = rand(n, k); % initialise V randomly
        //        
        // for iter = 1:maxiter
        // V = V.*((A'*U+eps)./(V*U'*U+eps)); % update V
        // U = U.*((A*V+eps)./(U*V'*V+eps)); % update U
        // C(1, iter) = norm((A-U*V'), 'fro'); % approximation quality
        // end

        double eps = 1e-9;

        // Seed U and V with initial values
        U = new DenseDoubleMatrix2D(A.rows(), k);
        V = new DenseDoubleMatrix2D(A.columns(), k);
        seedingStrategy.seed(A, U, V);

        // Temporary matrices
        DoubleMatrix2D T = new DenseDoubleMatrix2D(k, k);
        DoubleMatrix2D UT1 = new DenseDoubleMatrix2D(A.rows(), k);
        DoubleMatrix2D UT2 = new DenseDoubleMatrix2D(A.rows(), k);
        DoubleMatrix2D VT1 = new DenseDoubleMatrix2D(A.columns(), k);
        DoubleMatrix2D VT2 = new DenseDoubleMatrix2D(A.columns(), k);
        DoubleFunction plusEps = Functions.plus(eps);

        if (stopThreshold >= 0)
        {
            updateApproximationError();
        }

        for (int i = 0; i < maxIterations; i++)
        {
            // Update V
            U.zMult(U, T, 1, 0, true, false); // T <- U'U
            A.zMult(U, VT1, 1, 0, true, false); // VT1 <- A'U
            V.zMult(T, VT2, 1, 0, false, false); // VT2 <- VT
            VT1.assign(plusEps); // TODO: shift this to the dividing function?
            VT2.assign(plusEps);
            VT1.assign(VT2, Functions.DIV); // VT1 <- VT1 ./ VT2
            V.assign(VT1, Functions.MULT); // V <- V .* VT1

            // Update U
            V.zMult(V, T, 1, 0, true, false); // T <- V'V
            A.zMult(V, UT1, 1, 0, false, false); // UT1 <- AV
            U.zMult(T, UT2, 1, 0, false, false); // UT2 <- UT
            UT1.assign(plusEps);
            UT2.assign(plusEps);
            UT1.assign(UT2, Functions.DIV); // UT1 <- UT1 ./ UT2
            U.assign(UT1, Functions.MULT); // U <- U .* UT1

            iterationsCompleted++;
            if (stopThreshold >= 0)
            {
                if (updateApproximationError())
                {
                    break;
                }
            }
        }

        if (ordered)
        {
            order();
        }
    }

    public String toString()
    {
        return "NMF-ED-" + seedingStrategy.toString();
    }
}
