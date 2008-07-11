package org.carrot2.matrix.factorization.seeding;

/**
 * Creates random seeding strategies.
 */
public class RandomSeedingStrategyFactory implements SeedingStrategyFactory
{
    /** The random seed to be used */
    private int seed;

    /** If true, current system time will be used as the random seed */
    private boolean dateSeed;

    /**
     * Creates the factory with seeding based on current system time.
     */
    public RandomSeedingStrategyFactory()
    {
        this.dateSeed = true;
    }

    /**
     * Creates the factory with given seed value.
     */
    public RandomSeedingStrategyFactory(int seed)
    {
        this.seed = seed;
        this.dateSeed = false;
    }

    public SeedingStrategy createSeedingStrategy()
    {
        RandomSeedingStrategy seeding;

        if (dateSeed)
        {
            seeding = new RandomSeedingStrategy();
        }
        else
        {
            seeding = new RandomSeedingStrategy(seed);
        }

        return seeding;
    }

    /**
     * Returns the random seed to be used.
     * 
     */
    public int getSeed()
    {
        return seed;
    }

    /**
     * Sets the random seed to be used. Disables seeding with current system
     * time.
     * 
     * @param seed
     */
    public void setSeed(int seed)
    {
        this.seed = seed;
        this.dateSeed = false;
    }

    /**
     * Returns true if the current system time is used to generate seed.
     * 
     */
    public boolean getDateSeed()
    {
        return dateSeed;
    }

    /**
     * Set date seed to true to use current system time as random seed.
     * 
     * @param dateSeed
     */
    public void setDateSeed(boolean dateSeed)
    {
        this.dateSeed = dateSeed;
    }
    
    public String toString()
    {
        return "R";
    }
}