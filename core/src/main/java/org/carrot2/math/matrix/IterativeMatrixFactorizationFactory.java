/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.math.matrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrEnum;
import org.carrot2.math.mahout.matrix.DoubleMatrix2D;

/** A factory for {@link MatrixFactorization}s. */
public abstract class IterativeMatrixFactorizationFactory extends AttrComposite
    implements MatrixFactorizationFactory {
  /**
   * Number of iterations of matrix factorization to perform. The higher the required quality, the
   * more time-consuming clustering.
   */
  public final AttrEnum<FactorizationQuality> factorizationQuality =
      attributes.register(
          "factorizationQuality",
          AttrEnum.builder(FactorizationQuality.class)
              .label("Factorization quality")
              .defaultValue(FactorizationQuality.HIGH));

  /** The number of base vectors */
  protected int k;

  /** The default number of base vectors */
  protected static final int DEFAULT_K = 15;

  /** The maximum number of iterations the algorithm is allowed to complete */
  protected int maxIterations;

  /** The default number of maximum iterations */
  protected static final int DEFAULT_MAX_ITERATIONS = 15;

  /** The algorithm's stop threshold */
  protected double stopThreshold;

  /** The default stop threshold */
  protected static final double DEFAULT_STOP_THRESHOLD = -1;

  /** Matrix seeding strategy factory */
  protected SeedingStrategyFactory seedingFactory;

  /** Default matrix seeding strategy factory */
  protected static final SeedingStrategyFactory DEFAULT_SEEDING_FACTORY =
      new RandomSeedingStrategyFactory(0);

  /** Order base vectors according to their 'activity' */
  protected boolean ordered;

  protected static final boolean DEFAULT_ORDERED = true;

  public IterativeMatrixFactorizationFactory() {
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
  public void setK(int k) {
    this.k = k;
  }

  /** Returns the number of base vectors <i>k </i>. */
  public int getK() {
    return k;
  }

  /** */
  protected SeedingStrategy createSeedingStrategy() {
    return seedingFactory.createSeedingStrategy();
  }

  /** Returns the maximum number of iterations used by this factory. */
  public int getMaxIterations() {
    return maxIterations;
  }

  /** Sets the maximum number of iterations to be used by this factory. */
  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  /** Returns the stop threshold used by this factory. */
  public double getStopThreshold() {
    return stopThreshold;
  }

  /** Sets the stop threshold to be used by this factory. */
  public void setStopThreshold(double stopThreshold) {
    this.stopThreshold = stopThreshold;
  }

  /** Returns the {@link SeedingStrategyFactory} used by this factory. */
  public SeedingStrategyFactory getSeedingFactory() {
    return seedingFactory;
  }

  /** Sets the {@link SeedingStrategyFactory} to be used by this factory. */
  public void setSeedingFactory(SeedingStrategyFactory seedingFactory) {
    this.seedingFactory = seedingFactory;
  }

  /** Returns <code>true</code> when the factorization is set to generate an ordered basis. */
  public boolean isOrdered() {
    return ordered;
  }

  /** Set to <code>true</code> to generate an ordered basis. */
  public void setOrdered(boolean ordered) {
    this.ordered = ordered;
  }

  public void estimateIterationsNumber(int dimensions, DoubleMatrix2D termDocumentMatrix) {
    setK(dimensions);

    double[] coefficients =
        allKnownCoefficients.get(
            Arrays.asList(
                this.getClass(), this.getSeedingFactory().getClass(), factorizationQuality.get()));

    DoubleMatrix2D A = termDocumentMatrix;
    if (coefficients != null) {
      double columns = Math.sqrt(A.rows() * A.columns() / 2.8);
      if (columns < 50 || columns > 400 || getK() < 5 || getK() > 50) {
        // That's probably beyond our simplistic model
      } else {
        int iterations =
            (int) (columns * coefficients[0] + getK() * coefficients[1] + coefficients[2]);
        setMaxIterations((int) (iterations * 0.6));
      }
    }
  }

  private static Map<List<Object>, double[]> allKnownCoefficients;

  static {
    allKnownCoefficients = new HashMap<>();

    /* NMF-ED, Random seeding, level 1 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationEDFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.LOW),
        new double[] {-0.0166, 0.3333, 8.0000});

    /* NMF-ED, Random seeding, level 2 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationEDFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.MEDIUM),
        new double[] {-0.0175, 0.6, 12.0});

    /* NMF-ED, Random seeding, level 3 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationEDFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.HIGH),
        new double[] {-0.0186, 0.8222, 17.3555});

    /* NMF-KL, Random seeding, level 1 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationKLFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.LOW),
        new double[] {-0.0166, 0.3333, 8.0000});

    /* NMF-KL, Random seeding, level 2 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationKLFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.MEDIUM),
        new double[] {-0.0175, 0.6, 12.0});

    /* NMF-KL, Random seeding, level 3 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationKLFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.HIGH),
        new double[] {-0.0186, 0.8222, 17.3555});

    /* NMF-ED, KMeans seeding, level 1 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationEDFactory.class,
            KMeansSeedingStrategyFactory.class,
            FactorizationQuality.LOW),
        new double[] {-0.005, 0, 6});

    /* NMF-ED, KMeans seeding, level 2 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationEDFactory.class,
            KMeansSeedingStrategyFactory.class,
            FactorizationQuality.MEDIUM),
        new double[] {-0.005, 0, 10});

    /* NMF-ED, KMeans seeding, level 3 */
    allKnownCoefficients.put(
        Arrays.asList(
            NonnegativeMatrixFactorizationEDFactory.class,
            KMeansSeedingStrategyFactory.class,
            FactorizationQuality.HIGH),
        new double[] {-0.005, 0, 20});

    /* LNMF, Random seeding, level 1 */
    allKnownCoefficients.put(
        Arrays.asList(
            LocalNonnegativeMatrixFactorizationFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.LOW),
        new double[] {-0.014, 0.1, 7.5});

    /* LNMF, Random seeding, level 2 */
    allKnownCoefficients.put(
        Arrays.asList(
            LocalNonnegativeMatrixFactorizationFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.MEDIUM),
        new double[] {-0.0175, 0.15, 11.3333});

    /* LNMF, Random seeding, level 3 */
    allKnownCoefficients.put(
        Arrays.asList(
            LocalNonnegativeMatrixFactorizationFactory.class,
            RandomSeedingStrategyFactory.class,
            FactorizationQuality.HIGH),
        new double[] {-0.02333, 0.2, 16.8888});
  }
}
