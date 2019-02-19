
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.math.matrix;

import org.carrot2.math.mahout.matrix.DoubleMatrix2D;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrEnum;

/**
 * A factory for {@link IMatrixFactorization}s.
 */
public abstract class IterativeMatrixFactorizationFactory extends AttrComposite implements IMatrixFactorizationFactory
{
    /**
     * Factorization quality. The number of iterations of matrix factorization to perform.
     * The higher the required quality, the more time-consuming clustering.
     */
    public final AttrEnum<IterationNumberGuesser.FactorizationQuality> factorizationQuality =
        attributes.register("factorizationFactory", AttrEnum.builder(IterationNumberGuesser.FactorizationQuality.class)
            .label("Factorization quality")
            .defaultValue(IterationNumberGuesser.FactorizationQuality.HIGH));

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
    protected final static ISeedingStrategyFactory DEFAULT_SEEDING_FACTORY = new RandomSeedingStrategyFactory(0);

    /** Order base vectors according to their 'activity' */
    protected boolean ordered;
    protected static final boolean DEFAULT_ORDERED = true;

    public IterativeMatrixFactorizationFactory()
    {
        this.k = DEFAULT_K;
        this.maxIterations = DEFAULT_MAX_ITERATIONS;
        this.stopThreshold = DEFAULT_STOP_THRESHOLD;
        this.seedingFactory = DEFAULT_SEEDING_FACTORY;
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
     */
    public void setSeedingFactory(ISeedingStrategyFactory seedingFactory)
    {
        this.seedingFactory = seedingFactory;
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
     */
    public void setOrdered(boolean ordered)
    {
        this.ordered = ordered;
    }

    public void estimateIterationsNumber(int dimensions, DoubleMatrix2D termDocumentMatrix) {
        setK(dimensions);
        // TODO: this delegation is a bit awkward; should be pulled up to individual impls.?
        IterationNumberGuesser.setEstimatedIterationsNumber(
            this, termDocumentMatrix, factorizationQuality.get());
    }
}
