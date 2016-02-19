
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

package org.carrot2.core.benchmarks.memtime;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.junit.Test;

/**
 * 
 */
public class OpenSourceAlgorithmsBenchmark extends MemTimeBenchmark
{
    @Test
    public void evalBasicPreprocessing()
    {
        evalShortDocs("basic-preprocessing", BasicPreprocessing.class, MIN, MAX, STEP);
    }

    @Test
    public void evalSTC()
    {
        evalShortDocs("stc", STCClusteringAlgorithm.class, MIN, MAX, STEP);
    }

    /**
     * This test is currently ignored if NNI is not available.
     */
    @Test
    public void evalLingo()
    {
        evalShortDocs("lingo", LingoClusteringAlgorithm.class, MIN, MAX, STEP);
    }
}
