/*
 * IterativeMatrixFactorizationBase.java Created on 2004-06-20
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;
import cern.jet.math.*;

import com.stachoodev.matrix.factorization.seeding.*;

/**
 * @author stachoo
 */
public abstract class IterativeMatrixFactorizationBase extends
    MatrixFactorizationBase
{
    /** The desired number of base vectors */
    protected int k;
    protected static int DEFAULT_K = 30;

    /** The maximum number of iterations the algorithm is allowed to run */
    protected int maxIterations;
    protected static int DEFAULT_MAX_ITERATIONS = 50;

    /**
     * If the percentage decrease in approximation error becomes smaller than
     * <code>stopThreshold</code>, the algorithm will stop
     */
    protected double stopThreshold;
    protected static double DEFAULT_STOP_THRESHOLD = 0.0;

    /** Approximation error */
    protected double approximationError;

    /** Iteration counter */
    protected int iterationsCompleted;

    /** Seeding strategy */
    protected SeedingStrategy seedingStrategy;
    protected static SeedingStrategy DEFAULT_SEEDING_STRATEGY = new RandomSeedingStrategy(
        0);

    /**
     * @param A
     */
    public IterativeMatrixFactorizationBase(DoubleMatrix2D A)
    {
        super(A);

        this.k = DEFAULT_K;
        this.maxIterations = DEFAULT_MAX_ITERATIONS;
        this.stopThreshold = DEFAULT_STOP_THRESHOLD;
        this.seedingStrategy = DEFAULT_SEEDING_STRATEGY;
    }

    /**
     * Sets the number of base vectors <i>k </i>.
     * 
     * @param k the number of base vectors
     */
    public void setK(int k)
    {
        this.k = k;
    }

    /**
     * Returns the number of base vectors <i>k </i>.
     * 
     * @return
     */
    public int getK()
    {
        return k;
    }

    /**
     * @return true if the decrease in the approximation error is smaller than
     *         the <code>stopThreshold</code>
     */
    protected boolean updateApproximationError()
    {
        // Approximation error
        double newApproximationError = Algebra.DEFAULT.normF(U.zMult(V, null,
            1, 0, false, true).assign(A, Functions.minus));

        if (approximationError - newApproximationError < stopThreshold)
        {
            approximationError = newApproximationError;
            return true;
        }

        approximationError = newApproximationError;
        return false;
    }

    /**
     * Returns current {@link SeedingStrategy}.
     * 
     * @return
     */
    public SeedingStrategy getSeedingStrategy()
    {
        return seedingStrategy;
    }

    /**
     * Sets new {@link SeedingStrategy}.
     * 
     * @param seedingStrategy
     */
    public void setSeedingStrategy(SeedingStrategy seedingStrategy)
    {
        this.seedingStrategy = seedingStrategy;
    }

    /**
     * Returns the maximum number of iterations the algorithm is allowed to run.
     * 
     * @return Returns the maxIterations.
     */
    public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations the algorithm is allowed to run.
     * 
     * @param maxIterations
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    /**
     * Returns the algorithms <code>stopThreshold</code>. If the percentage
     * decrease in approximation error becomes smaller than
     * <code>stopThreshold</code>, the algorithm will stop.
     * 
     * @return
     */
    public double getStopThreshold()
    {
        return stopThreshold;
    }

    /**
     * Sets the algorithms <code>stopThreshold</code>. If the percentage
     * decrease in approximation error becomes smaller than
     * <code>stopThreshold</code>, the algorithm will stop.
     * 
     * @param
     */
    public void setStopThreshold(double stopThreshold)
    {
        this.stopThreshold = stopThreshold;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorization#getApproximationError()
     */
    public double getApproximationError()
    {
        return approximationError;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorization#getIterationsCompleted()
     */
    public int getIterationsCompleted()
    {
        return iterationsCompleted;
    }
}