
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.carrot2.core.DocumentSourceDescriptor;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.source.xml.XmlDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.URLResource;
import org.carrot2.workbench.core.ui.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.google.common.collect.ImmutableSet;

public class ProcessingJobTest extends TestCase
{
    private AttributeValueSet attributes;

    /*
     * This source ID must be available in the input suite.
     */
    private final static String SOURCE_XML = "test-xml";

    private final static String ALGO_BY_URL = "test-by-url";

    /**
     * Sources excluded from automatic Workbench tests, e.g. sources requiring some
     * specific software installed locally. We also exclude Yahoo APIs as they tend to 
     * fail often and we don't want them to cause the whole Workbench build to fail.
     */
    private final static Set<String> EXCLUDED_SOURCES = ImmutableSet.of("solr", "lucene",
        "wiki", "boss-images", "boss-web", "boss-news", "yahoo-news", "pubmed", "opensearch");

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        attributes = new AttributeValueSet("request");
        attributes.setAttributeValue("query", "data mining");

        IResource xmlInput = new URLResource(FileLocator.toFileURL(FileLocator.find(
            Platform.getBundle("org.carrot2.workbench.core.test"), new Path(
                "input/data_mining.xml"), null)));

        attributes.setAttributeValue(AttributeUtils
            .getKey(XmlDocumentSource.class, "xml"), xmlInput);
    }

    public void testDocumentSources() throws InterruptedException
    {
        for (DocumentSourceDescriptor wrapper : WorkbenchCorePlugin.getDefault()
            .getComponentSuite().getSources())
        {
            final String id = wrapper.getId();
            if (!EXCLUDED_SOURCES.contains(id))
            {
                runSearch(ALGO_BY_URL, id);
            }
        }
    }

    public void testClusteringAlgorithms() throws InterruptedException
    {
        for (ProcessingComponentDescriptor wrapper : WorkbenchCorePlugin.getDefault()
            .getComponentSuite().getAlgorithms())
        {
            runSearch(wrapper.getId(), SOURCE_XML);
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
        for (DocumentSourceDescriptor wrapper : WorkbenchCorePlugin.getDefault()
            .getComponentSuite().getSources())
        {
            performSaveAndRestore(wrapper.getId());
        }
    }

    private void performSaveAndRestore(String sourceId)
    {
        SearchInput search = new SearchInput(sourceId, ALGO_BY_URL, attributes);
        IMemento memento = XMLMemento.createWriteRoot("root");
        search.saveState(memento);

        SearchInput search2 = (SearchInput) new SearchInputFactory().createElement(
            memento).getAdapter(SearchInput.class);

        assertEquals(search.getAlgorithmId(), search2.getAlgorithmId());
        assertEquals(search.getSourceId(), search2.getSourceId());

        for (Entry<String, Object> entry : search2.getAttributeValueSet()
            .getAttributeValues().entrySet())
        {
            Map<String, Object> attributeValues = search.getAttributeValueSet()
                .getAttributeValues();
            assertTrue(attributeValues.containsKey(entry.getKey()));
            assertEquals(attributeValues.get(entry.getKey()), entry.getValue());
        }
    }
}
