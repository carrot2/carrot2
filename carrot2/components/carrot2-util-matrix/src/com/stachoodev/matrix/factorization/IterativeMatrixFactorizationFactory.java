/*
 * IterativeMatrixFactorizationFactory.java Created on 2004-06-17
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;
import com.stachoodev.matrix.factorization.seeding.*;

/**
 * @author stachoo
 */
public abstract class IterativeMatrixFactorizationFactory implements
    MatrixFactorizationFactory
{
    /** The number of base vectors */
    protected int k;

    /** The default number of base vectors */
    protected final static int DEFAULT_K = 20;

    /** The maximum number of iterations the algorithm is allowed to complete */
    protected int maxIterations;

    /** The default number of maxium iterations */
    protected final static int DEFAULT_MAX_ITERATIONS = 15;

    /** The algorithm's stop threshold */
    protected double stopThreshold;
    /** The default stop threshold */
    protected final static int DEFAULT_STOP_THRESHOLD = 0;

    /** Matrix seeding strategy factory */
    protected SeedingStrategyFactory seedingFactory;

    /** Default matrix seeding strategy factory */
    protected final static SeedingStrategyFactory DEFAULT_SEEDING_FACTORY = new RandomSeedingStrategyFactory(
        0);

    /** MatrixFactory to be used */
    protected DoubleFactory2D doubleFactory2D;

    /** The default MatrixFactory to be used */
    protected final static DoubleFactory2D DEFAULT_DOUBLE_FACTORY_2D = NNIDoubleFactory2D.nni;

    /**
     * 
     */
    public IterativeMatrixFactorizationFactory()
    {
        this.k = DEFAULT_K;
        this.maxIterations = DEFAULT_MAX_ITERATIONS;
        this.stopThreshold = DEFAULT_STOP_THRESHOLD;
        this.seedingFactory = DEFAULT_SEEDING_FACTORY;
        this.doubleFactory2D = DEFAULT_DOUBLE_FACTORY_2D;
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
     * Returns {@link RandomSeedingStrategy}with constant seed.
     * 
     * @return
     */
    protected SeedingStrategy createSeedingStrategy()
    {
        return seedingFactory.createSeedingStrategy();
    }

    /**
     * Returns the maximum number of iterations used by this factory.
     * 
     * @return
     */
    public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations to be used by this factory.
     * 
     * @param maxIterations
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    /**
     * Returns the stop threshold used by this factory.
     * 
     * @return
     */
    public double getStopThreshold()
    {
        return stopThreshold;
    }

    /**
     * Sets the stop threshold to be used by this factory.
     * 
     * @param stopThreshold
     */
    public void setStopThreshold(double stopThreshold)
    {
        this.stopThreshold = stopThreshold;
    }

    /**
     * Returns the {@link SeedingStrategyFactory}used by this factory.
     * 
     * @return
     */
    public SeedingStrategyFactory getSeedingFactory()
    {
        return seedingFactory;
    }

    /**
     * Sets the {@link SeedingStrategyFactory}to be used by this factory.
     * 
     * @param seedingFactory
     */
    public void setSeedingFactory(SeedingStrategyFactory seedingFactory)
    {
        this.seedingFactory = seedingFactory;
    }
    
    /**
     * Sets the {@link DoubleFactory2D}to be used to construct result matrices.
     * 
     * @return
     */
    public DoubleFactory2D getDoubleFactory2D()
    {
        return doubleFactory2D;
    }

    /**
     * Sets the <code>doubleFactory2D</code> to be used by this factory.
     *
     * @param doubleFactory2D
     */
    public void setDoubleFactory2D(DoubleFactory2D doubleFactory2D)
    {
        this.doubleFactory2D = doubleFactory2D;
    }
}