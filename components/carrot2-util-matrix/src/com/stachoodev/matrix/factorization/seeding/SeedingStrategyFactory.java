/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.matrix.factorization.seeding;

/**
 * A factory that produces objects implementing the {@link SeedingStrategy}
 * interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface SeedingStrategyFactory
{
    /**
     * Creates a {@link SeedingStrategy}.
     * @return
     */
    public SeedingStrategy createSeedingStrategy();
}