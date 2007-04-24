package org.carrot2.core.test;

import java.util.*;

import junit.framework.*;

import org.apache.log4j.*;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.*;

/**
 * A base class for {@link LocalInputComponent} test classes. This class provides some common infrastructure, e.g. a
 * local controller in which tests can be performed.
 * 
 * @author Stanislaw Osinski
 */
public abstract class LocalInputComponentTestBase extends TestCase
{
    public final static Logger log = Logger.getLogger(LocalInputComponentTestBase.class);

    /** A simple local controller in which the input under tests is embedded */
    protected LocalController controller;

    public LocalInputComponentTestBase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        controller = setUpController(getLocalInputFactory());
    }

    /**
     * Implement this method to return the factory of the input components under tests.
     */
    protected abstract LocalComponentFactory getLocalInputFactory();

    /**
     * Queries the input component with specified number of requested results.
     */
    public List query(String query, int requestedResults) throws Exception
    {
        Map params = new HashMap();
        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, Integer.toString(requestedResults));
        return query(query, params);
    }

    /**
     * Queries the input component with custom parameters.
     */
    public List query(String query, Map params) throws Exception
    {
        return ((ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult()).documents;
    }

    /**
     * Sets up a simple (input-output) controller for testing the input component.
     */
    protected LocalControllerBase setUpController(LocalComponentFactory inputFactory) throws Exception
    {
        LocalControllerBase controller;

        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new ArrayOutputComponent();
            }
        };

        // Register with the controller
        controller = new LocalControllerBase();
        controller.addLocalComponentFactory("output", outputFactory);
        controller.addLocalComponentFactory("input", inputFactory);

        // Create and register the process
        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        controller.addProcess("testprocess", process);

        return controller;
    }

    /**
     * 
     */
    protected void performQuery(String query, int requestedResults, int expectedResults) throws Exception
    {
        performQuery(query, requestedResults, new Range(expectedResults, expectedResults));
    }

    /**
     * 
     */
    protected void performQuery(String query, int requestedResults, Range expectedResultsRange)
        throws Exception
    {
        final long start = System.currentTimeMillis();
        List results = query(query, requestedResults);
        final long end = System.currentTimeMillis();
        log.info("Query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired (" + results.size() + ") not in " + expectedResultsRange, expectedResultsRange.isIn(results.size()));
    }

    /**
     * 
     */
    protected void performIdUniquenessTest(String query, int requestedResults) throws Exception
    {
        List results = query(query, requestedResults);

        Set ids = new HashSet();
        for (Iterator it = results.iterator(); it.hasNext();)
        {
            RawDocument doc = (RawDocument) it.next();
            ids.add(doc.getId());
        }

        assertEquals("Number of unique ids equal to the number of results", ids.size(), results.size());
    }
}
