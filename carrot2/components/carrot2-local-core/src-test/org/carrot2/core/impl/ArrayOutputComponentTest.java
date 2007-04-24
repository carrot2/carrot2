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
