
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.impl;

import org.carrot2.core.LocalControllerBase;
import org.carrot2.core.test.ClusteringProcessTestBase;
import org.carrot2.core.test.Range;

/**
 * Tests both {@link XmlStreamInputComponent} and {@link ArrayOutputComponent}.
 * 
 * @author Dawid Weiss
 */
public class ArrayOutputComponentTest extends ClusteringProcessTestBase
{
    public ArrayOutputComponentTest(String testName)
    {
        super(testName);
    }

    /**
     * 
     */
    protected String [] getFiltersChain(LocalControllerBase controller)
    {
        return new String [] {/* empty, just passthrough from input to output */};
    }

    /**
     * Just test the pipeline, do nothing else.
     */
    public void testTestingPipeline() throws Exception
    {
        assertResultsInRange("n/a", 100, Range.exact(100), Range.exact(28), null);
    }
}
