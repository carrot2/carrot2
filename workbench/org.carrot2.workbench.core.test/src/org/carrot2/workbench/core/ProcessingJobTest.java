package org.carrot2.workbench.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.carrot2.source.xml.XmlDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.URLResource;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.ui.SearchParameters;
import org.eclipse.core.runtime.*;

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

    public void testXmlAndByUrl() throws InterruptedException, IOException
    {
        Resource xmlInput =
            new URLResource(FileLocator.toFileURL(FileLocator.find(Platform
                .getBundle("org.carrot2.workbench.core.test"), new Path(
                "input/data_mining.xml"), null)));
        attributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"), xmlInput);
        runSearch("org.carrot2.algorithm.synthetic.byUrl", "org.carrot2.source.xml.xml");
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
