package org.carrot2.matrix.factorization.seeding;

/**
 * A factory that produces objects implementing the {@link SeedingStrategy}
 * interface.
 */
public interface SeedingStrategyFactory
{
    /**
     * Creates a {@link SeedingStrategy}.
     */
    public SeedingStrategy createSeedingStrategy();
}