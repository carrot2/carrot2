
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

package org.carrot2.matrix.factorization;

import org.carrot2.matrix.NNIDoubleFactory2D;
import org.carrot2.matrix.factorization.seeding.*;

import org.apache.mahout.math.matrix.DoubleFactory2D;

/**
 * A factory for {@link IMatrixFactorization}s.
 */
@SuppressWarnings("deprecation")
public abstract class IterativeMatrixFactorizationFactory implements
    IMatrixFactorizationFactory
{
    /** The number of base vectors */
    protected int k;

    /** The default number of base vectors */
    protected final static int DEFAULT_K = 15;

    /** The maximum number of iterations the algorithm is allowed to complete */
    protected int maxIterations;

    /** The default number of maximum iterations */
    protected final static int DEFAULT_MAX_ITERATIONS = 15;

    /** The algorithm's stop threshold */
    protected double stopThreshold;

    /** The default stop threshold */
    protected final static double DEFAULT_STOP_THRESHOLD = -1;

    /** Matrix seeding strategy factory */
    protected ISeedingStrategyFactory seedingFactory;

    /** Default matrix seeding strategy factory */
    protected final static ISeedingStrategyFactory DEFAULT_SEEDING_FACTORY = new RandomSeedingStrategyFactory(
        0);

    /** MatrixFactory to be used */
    protected DoubleFactory2D doubleFactory2D;

    /** The default MatrixFactory to be used */
    protected final static DoubleFactory2D DEFAULT_DOUBLE_FACTORY_2D = NNIDoubleFactory2D.nni;

    /** Order base vectors according to their 'activity' */
    protected boolean ordered;
    protected static final boolean DEFAULT_ORDERED = true;

    public IterativeMatrixFactorizationFactory()
    {
        this.k = DEFAULT_K;
        this.maxIterations = DEFAULT_MAX_ITERATIONS;
        this.stopThreshold = DEFAULT_STOP_THRESHOLD;
        this.seedingFactory = DEFAULT_SEEDING_FACTORY;
        this.doubleFactory2D = DEFAULT_DOUBLE_FACTORY_2D;
        this.ordered = DEFAULT_ORDERED;
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
     */
    public int getK()
    {
        return k;
    }

    /**
     * Returns {@link RandomSeedingStrategy} with constant seed.
     */
    protected ISeedingStrategy createSeedingStrategy()
    {
        return seedingFactory.createSeedingStrategy();
    }

    /**
     * Returns the maximum number of iterations used by this factory.
     */
    public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations to be used by this factory.
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    /**
     * Returns the stop threshold used by this factory.
     */
    public double getStopThreshold()
    {
        return stopThreshold;
    }

    /**
     * Sets the stop threshold to be used by this factory.
     */
    public void setStopThreshold(double stopThreshold)
    {
        this.stopThreshold = stopThreshold;
    }

    /**
     * Returns the {@link ISeedingStrategyFactory} used by this factory.
     */
    public ISeedingStrategyFactory getSeedingFactory()
    {
        return seedingFactory;
    }

    /**
     * Sets the {@link ISeedingStrategyFactory} to be used by this factory.
     * 
     * @param seedingFactory
     */
    public void setSeedingFactory(ISeedingStrategyFactory seedingFactory)
    {
        this.seedingFactory = seedingFactory;
    }

    /**
     * Sets the <code>DoubleFactory2D</code> to be used to construct result matrices.
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

    /**
     * Returns <code>true</code> when the factorization is set to generate an ordered
     * basis.
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Set to <code>true</code> to generate an ordered basis.
     * 
     * @param ordered
     */
    public void setOrdered(boolean ordered)
    {
        this.ordered = ordered;
    }
}
