/*
 * SeedingStrategyFactory.java Created on 2004-06-20
 */
package com.stachoodev.matrix.factorization.seeding;

/**
 * A factory that produces objects implementing the {@link SeedingStrategy}
 * interface.
 * 
 * @author stachoo
 */
public interface SeedingStrategyFactory
{
    /**
     * Creates a {@link SeedingStrategy}.
     * @return
     */
    public SeedingStrategy createSeedingStrategy();
}