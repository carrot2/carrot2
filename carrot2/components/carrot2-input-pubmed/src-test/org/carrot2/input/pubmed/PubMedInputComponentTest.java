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

package org.carrot2.input.pubmed;

import java.util.*;

import org.apache.log4j.*;
import org.carrot2.core.*;
import org.carrot2.core.impl.*;


public class PubMedInputComponentTest
    extends junit.framework.TestCase
{
    private final static Logger log = Logger
            .getLogger(PubMedInputComponentTest.class);

    final LocalComponentFactory inputFactory = new LocalComponentFactory() {
        public LocalComponent getInstance()
        {
            return new PubMedInputComponent();
        }
    };


    public PubMedInputComponentTest(String s)
    {
        super(s);
    }


    public void testNoHitsQuery()
        throws Exception
    {
        LocalControllerBase controller = setUpController(inputFactory);
        String query = "asdhasd alksjdhar swioer";
        final long start = System.currentTimeMillis();
        HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
                new Integer(50));
        List results = ((ArrayOutputComponent.Result)controller.query(
                "testprocess", query, reqContext).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("PubMed query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertEquals("Results acquired from PubMed" + ":" + results.size(), 0,
                results.size());
    }

    
    public void testSmallQuery()
    throws Exception
    {
        LocalControllerBase controller = setUpController(inputFactory);
        String query = "test";
        final long start = System.currentTimeMillis();
        HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
                new Integer(50));
        List results = ((ArrayOutputComponent.Result)controller.query(
                "testprocess", query, reqContext).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("PubMed query time: " + (end - start) + " ms.");
        
        // the results should contain some documents.
        assertEquals("Results acquired from PubMed" + ":" + results.size(), 50,
                results.size());
    }
    

    public void testMediumQuery()
        throws Exception
    {
        LocalControllerBase controller = setUpController(inputFactory);
        String query = "test";
        final long start = System.currentTimeMillis();
        HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
                new Integer(100));
        List results = ((ArrayOutputComponent.Result)controller.query(
                "testprocess", query, reqContext).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("PubMed query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertEquals("Results acquired from PubMed" + ":" + results.size(),
                100, results.size());
    }


    public void testLargeQuery()
        throws Exception
    {
        LocalControllerBase controller = setUpController(inputFactory);
        String query = "test";
        final long start = System.currentTimeMillis();
        HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
                new Integer(400));
        List results = ((ArrayOutputComponent.Result)controller.query(
                "testprocess", query, reqContext).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("PubMed query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertEquals("Results acquired from PubMed" + ":" + results.size(),
                400, results.size());
    }


    protected LocalControllerBase setUpController(
            LocalComponentFactory inputFactory)
        throws Exception
    {
        LocalControllerBase controller;

        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactory() {
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
