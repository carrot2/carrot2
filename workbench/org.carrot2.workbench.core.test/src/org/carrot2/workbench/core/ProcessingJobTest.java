package org.carrot2.workbench.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.carrot2.source.xml.XmlDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.URLResource;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.carrot2.workbench.core.helpers.ComponentWrapper;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.ui.SearchParameters;
import org.carrot2.workbench.core.ui.SearchParametersFactory;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

public class ProcessingJobTest extends TestCase
{
    private Map<String, Object> attributes;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        attributes = new HashMap<String, Object>();
        attributes.put("query", "data mining");
        Resource xmlInput =
            new URLResource(FileLocator.toFileURL(FileLocator.find(Platform
                .getBundle("org.carrot2.workbench.core.test"), new Path(
                "input/data_mining.xml"), null)));
        attributes.put(AttributeUtils.getKey(XmlDocumentSource.class, "xml"), xmlInput);
    }

    public void testDocumentSources() throws InterruptedException
    {
        for (ComponentWrapper wrapper : ComponentLoader.SOURCE_LOADER.getComponents())
        {
            runSearch("org.carrot2.algorithm.synthetic.byUrl", wrapper.getId());
        }
    }

    public void testClusteringAlgorithms() throws InterruptedException
    {
        for (ComponentWrapper wrapper : ComponentLoader.ALGORITHM_LOADER.getComponents())
        {
            if (wrapper.getId().equals("org.carrot2.workbench.core.test.algorithm1"))
            {
                continue;
            }
            runSearch(wrapper.getId(), "org.carrot2.source.xml.xml");
        }
    }

    private void runSearch(String algorithmId, String sourceId)
        throws InterruptedException
    {
        SearchParameters search = new SearchParameters(sourceId, algorithmId, attributes);
        ProcessingJob job = new ProcessingJob("name", search);
        job.schedule();
        job.join();
        if (!job.getResult().isOK())
        {
            throw new RuntimeException(String.format(
                "Job failed. SourceId: %s, AlgorithmId: %s", sourceId, algorithmId), job
                .getResult().getException());
        }
    }

    public void testSavingAndRestoringOfRequiredAttributes()
    {
        for (ComponentWrapper wrapper : ComponentLoader.SOURCE_LOADER.getComponents())
        {
            performSaveAndRestore(wrapper.getId());
        }
    }

    private void performSaveAndRestore(String sourceId)
    {
        SearchParameters search =
            new SearchParameters(sourceId, "org.carrot2.algorithm.synthetic.byUrl",
                attributes);
        IMemento memento = XMLMemento.createWriteRoot("root");
        search.saveState(memento);
        SearchParameters search2 =
            (SearchParameters) new SearchParametersFactory().createElement(memento)
                .getAdapter(SearchParameters.class);
        assertEquals(search.getAlgorithmId(), search2.getAlgorithmId());
        assertEquals(search.getSourceId(), search2.getSourceId());
        for (Entry<String, Object> entry : search2.getAttributes().entrySet())
        {
            assertTrue(search.getAttributes().containsKey(entry.getKey()));
            assertEquals(search.getAttributes().get(entry.getKey()), entry.getValue());
        }
    }

}
