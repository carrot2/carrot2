
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

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.matrix.MatrixUtils;

/**
 * Performs matrix factorization using the Non-negative Matrix Factorization by
 * minimization of Kullback-Leibler divergence between A and UV' and multiplicative
 * updating.
 */
public class NonnegativeMatrixFactorizationKL extends IterativeMatrixFactorizationBase
{
    /**
     * Creates the NonnegativeMatrixFactorizationKL object for matrix A. Before accessing
     * results, perform computations by calling the {@link #compute()} method.
     * 
     * @param A matrix to be factorized
     */
    public NonnegativeMatrixFactorizationKL(DoubleMatrix2D A)
    {
        super(A);
    }

    public void compute()
    {
        // Prototype Matlab code for the NMF-KL
        //        
        // function [U, V, C] = nmf-kl(A)
        // [m, n] = size(A);
        // k = 2; % the desired number of base vectors
        // maxiter = 50; % the number of iterations
        // eps = 1e-9; % machine epsilon
        //        
        // U = rand(m, k); % initialise U randomly
        // V = rand(n, k); % initialise V randomly
        // O = ones(m, m); % a matrix of ones
        //        
        // for iter = 1:maxiter
        // V = V.*(((A+eps)./(U*V'+eps))'*U); % update V
        // U = U.*(((A+eps)./(U*V'+eps))*V); % update U
        // U = U./(O*U); % normalise U's columns
        // C(1, iter) = norm((A-U*V'), 'fro'); % approximation quality
        // end

        int m = A.rows();
        int n = A.columns();
        double eps = 1e-9;

        // Seed U and V with initial values
        U = new DenseDoubleMatrix2D(m, k);
        V = new DenseDoubleMatrix2D(n, k);
        seedingStrategy.seed(A, U, V);

        // Temporary matrices
        DoubleMatrix2D Aeps = A.copy();
        Aeps.assign(Functions.plus(eps));
        DoubleMatrix2D UV = new DenseDoubleMatrix2D(m, n);
        DoubleMatrix2D VT = new DenseDoubleMatrix2D(n, k);
        DoubleMatrix2D UT = new DenseDoubleMatrix2D(m, k);
        double [] work = new double [U.columns()];

        // Colt functions
        DoubleDoubleFunction invDiv = Functions.swapArgs(Functions.DIV);
        DoubleFunction plusEps = Functions.plus(eps);

        if (stopThreshold >= 0)
        {
            updateApproximationError();
        }

        for (int i = 0; i < maxIterations; i++)
        {
            // Update V
            U.zMult(V, UV, 1, 0, false, true); // UV <- U*V'
            UV.assign(plusEps); // UV <- UV + eps
            UV.assign(Aeps, invDiv); // UV <- Aeps ./ UV
            UV.zMult(U, VT, 1, 0, true, false); // VT <- UV' * U
            V.assign(VT, Functions.MULT); // V <- V .* VT

            // Update U
            U.zMult(V, UV, 1, 0, false, true); // UV <- U*V'
            UV.assign(plusEps); // UV <- UV + eps
            UV.assign(Aeps, invDiv); // UV <- Aeps ./ UV
            UV.zMult(V, UT, 1, 0, false, false); // UT <- UV * V
            U.assign(UT, Functions.MULT); // U <- U .* UT

            MatrixUtils.normalizeColumnL1(U, work);

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
        return "NMF-KL-" + seedingStrategy.toString();
    }
}
