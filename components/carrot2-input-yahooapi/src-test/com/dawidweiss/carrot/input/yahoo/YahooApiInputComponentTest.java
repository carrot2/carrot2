
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.input.yahoo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.ArrayOutputComponent;

public class YahooApiInputComponentTest extends junit.framework.TestCase {
    private final static Logger log = Logger.getLogger(YahooApiInputComponentTest.class);

    public YahooApiInputComponentTest(String s) {
        super(s);
    }

    public void testJanWeglarzQuery() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent();
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "Jan Węglarz";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("Yahoo query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertEquals("Results acquired from Yahoo"
                + ":" + results.size(), 100, results.size());
    }

    public void testSiteQuery() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent();
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "weiss";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("YahooAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo" + ":" + results.size(), results.size() > 0);
    }

    public void testStartPositionIncorrect() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent();
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "webstart splash colors";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("YahooAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo" + ":" + results.size(), results.size() > 0);
    }
    
	protected LocalControllerBase setUpController(LocalComponentFactory inputFactory) throws Exception {
		LocalControllerBase controller;
		
        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
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

	public void testMediumQuery() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent();
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "dawid weiss ant styler docbook poznan";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("YahooAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo"
                + ":" + results.size(), results.size() > 0 && results.size() < 100);
	}
	
	public void testEmptyQuery() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent();
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "duiogig oiudgisugviw siug iugw iusviuwg";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("Yahoo query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo"
                + ":" + results.size(), results.size() == 0);
	}

    public void testResultsRequested() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent();
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "apache";
        final long start = System.currentTimeMillis();
        HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(50));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("Yahoo query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        log.debug("Results acquired from Yahoo: " + results.size());
        assertEquals("Results acquired from Yahoo is 50?"
                + ":" + results.size(), 50, results.size());
    }    

	public void testApacheAntQuery() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new YahooApiInputComponent();
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "apache ant";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("Yahoo query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Yahoo"
                + ":" + results.size(), results.size() > 0);
        log.debug("Results acquired from Yahoo: " + results.size());

        HashSet urls = new HashSet();
        for (Iterator i = results.iterator(); i.hasNext(); ) {
            RawDocument rd = (RawDocument) i.next();
            // Check the URL.
            try {
                new URL(rd.getUrl());
            } catch (MalformedURLException e) {
                fail("Snippet reader failure (malformed URL): "
                        + rd.toString());
            }
            urls.add(rd.getUrl());
        }

        assertTrue(urls.contains("http://ant.apache.org/"));

        assertEquals(100, results.size());
	}
}
