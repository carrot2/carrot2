package org.carrot2.workbench.core;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.ui.SearchParameters;

public class ProcessingJobTest extends TestCase
{
    private Map<String, Object> attributes;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        attributes = new HashMap<String, Object>();
        attributes.put("query", "data mining");
    }

    public void testYahooAndByUrl() throws InterruptedException
    {
        runSearch("org.carrot2.algorithm.synthetic.byUrl",
            "org.carrot2.source.yahoo.yahoo");
    }

    private void runSearch(String algorithmId, String sourceId)
        throws InterruptedException
    {
        SearchParameters search = new SearchParameters(sourceId, algorithmId, attributes);
        ProcessingJob job = new ProcessingJob("name", search);
        job.schedule();
        job.join();
        assertTrue(String.format("Job failed. SourceId: %s, AlgorithmId: %s", sourceId,
            algorithmId), job.getResult().isOK());
    }

}
