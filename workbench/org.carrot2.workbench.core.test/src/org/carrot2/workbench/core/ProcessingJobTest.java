package org.carrot2.workbench.core;

import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.carrot2.source.xml.XmlDocumentSource;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.URLResource;
import org.carrot2.workbench.core.ui.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

public class ProcessingJobTest extends TestCase
{
    private AttributeValueSet attributes;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        attributes = new AttributeValueSet("request");
        attributes.setAttributeValue("query", "data mining");

        Resource xmlInput = new URLResource(FileLocator.toFileURL(FileLocator.find(
            Platform.getBundle("org.carrot2.workbench.core.test"), new Path(
                "input/data_mining.xml"), null)));

        attributes.setAttributeValue(AttributeUtils
            .getKey(XmlDocumentSource.class, "xml"), xmlInput);
    }

    public void testDocumentSources() throws InterruptedException
    {
        for (ExtensionImpl wrapper : WorkbenchCorePlugin.getDefault()
            .getSources().getImplementations())
        {
            runSearch("org.carrot2.algorithm.synthetic.byUrl", wrapper.id);
        }
    }

    public void testClusteringAlgorithms() throws InterruptedException
    {
        for (ExtensionImpl wrapper : WorkbenchCorePlugin.getDefault()
            .getAlgorithms().getImplementations())
        {
            if (wrapper.id.equals("org.carrot2.workbench.core.test.algorithm1"))
            {
                continue;
            }

            runSearch(wrapper.id, "org.carrot2.source.xml.xml");
        }
    }

    private void runSearch(String algorithmId, String sourceId)
        throws InterruptedException
    {
        SearchInput input = new SearchInput(sourceId, algorithmId, attributes);
        SearchResult result = new SearchResult(input);

        SearchJob job = new SearchJob(result);
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
        for (ExtensionImpl wrapper : WorkbenchCorePlugin.getDefault()
            .getSources().getImplementations())
        {
            performSaveAndRestore(wrapper.id);
        }
    }

    private void performSaveAndRestore(String sourceId)
    {
        SearchInput search = new SearchInput(sourceId,
            "org.carrot2.algorithm.synthetic.byUrl", attributes);
        IMemento memento = XMLMemento.createWriteRoot("root");
        search.saveState(memento);

        SearchInput search2 = (SearchInput) new SearchInputFactory().createElement(
            memento).getAdapter(SearchInput.class);

        assertEquals(search.getAlgorithmId(), search2.getAlgorithmId());
        assertEquals(search.getSourceId(), search2.getSourceId());

        for (Entry<String, Object> entry : search2.getAttributeValueSet().getAttributeValues().entrySet())
        {
            Map<String, Object> attributeValues = search.getAttributeValueSet().getAttributeValues();
            assertTrue(attributeValues.containsKey(entry.getKey()));
            assertEquals(attributeValues.get(entry.getKey()), entry.getValue());
        }
    }

    public void testIfMetadataExists()
    {
        for (ExtensionImpl wrapper : WorkbenchCorePlugin.getDefault()
            .getSources().getImplementations())
        {
            buildBindableDescriptor(wrapper);
        }

        for (ExtensionImpl wrapper : WorkbenchCorePlugin.getDefault()
            .getAlgorithms().getImplementations())
        {
            buildBindableDescriptor(wrapper);
        }
    }

    private void buildBindableDescriptor(ExtensionImpl wrapper)
    {
        if (wrapper.id.equals("org.carrot2.workbench.core.test.algorithm1"))
        {
            return;
        }
        BindableDescriptorBuilder.buildDescriptor(wrapper.getInstance());
    }
}
