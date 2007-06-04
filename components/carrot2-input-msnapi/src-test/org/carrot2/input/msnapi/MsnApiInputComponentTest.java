/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.msnapi;

import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayOutputComponent;

/**
 * Test {@link MsnApiInputComponent}.
 * 
 * @author Dawid Weiss
 */
public class MsnApiInputComponentTest extends junit.framework.TestCase
{
    private final static Logger log = Logger.getLogger(MsnApiInputComponentTest.class);

    private LocalControllerBase controller;

    public MsnApiInputComponentTest(String s)
    {
        super(s);
    }

    public void setUp() throws Exception
    {
        final LocalComponentFactory inputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new MsnApiInputComponent();
            }
        };
        this.controller = setUpController(inputFactory);
    }

    public void testLargeQuery() throws Exception
    {
        String query = "windows";
        final long start = System.currentTimeMillis();

        final HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(200));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext)
            .getQueryResult()).documents;

        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results: " + results.size(), 
            /* Allow certain inconsistencies -- MSN sometimes returns fewer results than requested. */
            results.size() >= 170 && results.size() <= 200);
    }
    
    public void testVeryLargeQuery() throws Exception
    {
        String query = "clinton";
        final long start = System.currentTimeMillis();
        
        final HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(500));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext)
            .getQueryResult()).documents;
        
        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");
        
        // the results should contain some documents.
        assertTrue("Results: " + results.size(), 
            /* Allow certain inconsistencies -- MSN sometimes returns fewer results than requested. */
            results.size() >= 400 && results.size() <= 500);
    }

    public void testDawidWeissQuery() throws Exception
    {
        String query = "Stanislaw Osinski";
        final HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(150));

        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext)
            .getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results: " + results.size(),
            /* Allow certain inconsistencies -- MSN sometimes returns fewer results than requested. */
            results.size() >= 90 && results.size() <= 150);
    }

    public void testMediumQuery() throws Exception
    {
        String query = "dawid weiss ant styler docbook poznan";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap())
            .getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results: " + results.size(), results.size() > 0 && results.size() < 100);
    }

    public void testEmptyQuery() throws Exception
    {
        String query = "duiogig oiudgisugviw siug iugw iusviuwg";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap())
            .getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results: " + results.size(), results.size() == 0);
    }

    public void testResultsRequested() throws Exception
    {
        final String query = "apache";
        final HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(150));

        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext)
            .getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results: " + results.size(), results.size() > 140 && results.size() <= 150);
    }

    
    public void testUniqueIds() throws Exception
    {
        final String query = "apache";
        final HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(150));
        
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext)
            .getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");
        
        Set ids = new HashSet();
        for (Iterator it = results.iterator(); it.hasNext();)
        {
            RawDocument doc = (RawDocument) it.next();
            ids.add(doc.getId());
        }
        
        // the results should contain some documents.
        assertEquals(ids.size(), results.size());
    }
    
    public void testStartFromBug() throws Exception
    {
        final String query = "clustering";
        final HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(
            MsnApiInputComponent.MAXIMUM_RESULTS_PERQUERY * 2));

        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext)
            .getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("MSN query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        for (int i = 0; i < results.size() / 2; i++)
        {
            final String summary = ((RawDocument) results.get(i)).getSnippet() + "";
            final String summaryOffset = ((RawDocument) results.get(i + results.size() / 2)).getSnippet() + "";

            if (!summary.equals(summaryOffset))
            {
                return;
            }
        }

        fail();
    }

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
}