package org.carrot2.core.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.core.impl.XmlStreamInputComponent;
import org.carrot2.util.PerformanceLogger;

/**
 * A base class for testing clustering algorithms. This class contains utility code for setting up a
 * {@link LocalController} and passing through XML data sets.
 * 
 * @author Dawid Weiss
 */
public abstract class ClusteringProcessTestBase extends TestCase
{
    /** Internal name of the test process. */
    private static final String XML_INPUT_PROCESS_ID = ".internal.process";

    /** Logger instance attached to this test. */
    public final Logger log = Logger.getLogger(this.getClass());

    /** A local controller instance. */
    private LocalControllerBase controller;

    /**
     * JUnit-specific constructor.
     */
    public ClusteringProcessTestBase(String testName)
    {
        super(testName);
    }

    /**
     * 
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        controller = setUpController();
    }

    /**
     * 
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        controller = null;
    }

    /**
     * Set up your own filters and add them to the controller (if needed).
     */
    protected void addCustomComponents(LocalControllerBase controller)
    {
        // do nothing by default.
    }

    /**
     * Implement this method and return the filters chain used for clustering.
     */
    protected abstract String [] getFiltersChain(LocalControllerBase controller);

    /**
     * Sets up a simple (input-output) controller for testing the input component.
     */
    private LocalControllerBase setUpController() throws Exception
    {
        final LocalControllerBase controller = new LocalControllerBase();
        controller.setComponentAutoload(true);

        // Create and register the process
        final LocalProcessBase process = new LocalProcessBase();
        process.setInput("input-xml-stream");
        process.setOutput("output-array");

        final String [] filters = getFiltersChain(controller);
        for (int i = 0; i < filters.length; i++)
        {
            process.addFilter(filters[i]);
        }

        controller.addProcess(XML_INPUT_PROCESS_ID, process);

        return controller;
    }

    /**
     * Processes a sample XML file.
     * 
     * @see #assertResultsInRange(InputStream, String, int, Range, Range, Map)
     */
    protected final void assertResultsInRange(String query, int requestedDocuments, Range documents, Range clusters,
        Map requestParameters) throws Exception
    {
        if (requestParameters == null)
        {
            requestParameters = new HashMap();
        }
        if (requestedDocuments > 1000)
        {
            fail("There is only a 1000 documents in the test input.");
        }

        final InputStream inputStream = ClusteringProcessTestBase.class.getResourceAsStream("test-input-1.xml");
        assertResultsInRange(inputStream, query, requestedDocuments, documents, clusters, requestParameters);
    }

    /**
     * Assert the processing of an input XML stream in Carrot2 format causes no error and the results are within the
     * given ranges (documents and clusters).
     */
    protected final void assertResultsInRange(InputStream inputStream, String query, int requestedDocuments,
        Range docsRange, Range clustersRange, Map requestParameters) throws Exception
    {
        // Set up the test input.
        requestParameters.put(XmlStreamInputComponent.XML_STREAM, inputStream);
        requestParameters.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(requestedDocuments));

        final PerformanceLogger perfLogger = new PerformanceLogger(Level.INFO, log);
        try
        {
            perfLogger.start("Test clustering started.");
            final ProcessingResult processingResult = this.controller.query(XML_INPUT_PROCESS_ID, query,
                requestParameters);
            assertNotNull("Result expected.", processingResult);

            final ArrayOutputComponent.Result result = (ArrayOutputComponent.Result) processingResult.getQueryResult();
            final int documentCount = (result.documents != null ? result.documents.size() : 0);
            final int clusterCount = (result.clusters != null ? result.clusters.size() : 0);
            perfLogger.end("Acquired " + documentCount + " documents and " + clusterCount + " clusters.");

            if (docsRange != null)
            {
                assertTrue("Document count " + documentCount + " not in range " + docsRange, docsRange
                    .isIn(documentCount));
            }
            if (clustersRange != null)
            {
                assertTrue("Clusters count " + clusterCount + " not in range " + clustersRange, clustersRange
                    .isIn(clusterCount));
            }
        }
        finally
        {
            perfLogger.reset();
        }
    }
}
