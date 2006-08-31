
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

package org.carrot2.input.googleapi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayOutputComponent;

public class GoogleApiInputComponentTest extends junit.framework.TestCase {
    private final static Logger log = Logger.getLogger(GoogleApiInputComponentTest.class);

    public GoogleApiInputComponentTest(String s) {
        super(s);
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
    	final GoogleKeysPool pool = new GoogleKeysPool();
    	pool.addKeys(new File("keypool"), ".key");
        
        if (pool.getKeysTotal() == 0) {
            log.error("No available google api keys.");
            return;
        }

        LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new GoogleApiInputComponent(pool);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "dawid weiss ant styler docbook poznan";
        final long start = System.currentTimeMillis();
        ArrayOutputComponent.Result all = (ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult();
        List results = all.documents;
        final long end = System.currentTimeMillis();
        log.info("GoogleAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Google"
                + ":" + results.size(), results.size() > 0 && results.size() < 100);
	}
	
	public void testEmptyQuery() throws Exception {
    	final GoogleKeysPool pool = new GoogleKeysPool();
    	pool.addKeys(new File("keypool"), ".key");

        if (pool.getKeysTotal() == 0) {
            log.error("No available google api keys.");
            return;
        }
        
        LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new GoogleApiInputComponent(pool);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "duiogig oiudgisugviw siug iugw iusviuwg";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("GoogleAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Google"
                + ":" + results.size(), results.size() == 0);
	}

    public void testResultsRequested() throws Exception {
        final GoogleKeysPool pool = new GoogleKeysPool();
        pool.addKeys(new File("keypool"), ".key");

        if (pool.getKeysTotal() == 0) {
            log.error("No available google api keys.");
            return;
        }

        LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new GoogleApiInputComponent(pool);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "apache";
        final long start = System.currentTimeMillis();
        HashMap reqContext = new HashMap();
        reqContext.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(50));
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, reqContext).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("GoogleAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        log.debug("Results acquired from Google: " + results.size());
        assertEquals("Results acquired from Google is 50?"
                + ":" + results.size(), 50, results.size());
    }    
    
	public void testApacheAntQuery() throws Exception {
    	final GoogleKeysPool pool = new GoogleKeysPool();
    	pool.addKeys(new File("keypool"), ".key");

        if (pool.getKeysTotal() == 0) {
            log.error("No available google api keys.");
            return;
        }

        LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new GoogleApiInputComponent(pool);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "apache ant";
        final long start = System.currentTimeMillis();
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("GoogleAPI query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertTrue("Results acquired from Google"
                + ":" + results.size(), results.size() > 0);
        log.debug("Results acquired from Google: " + results.size());

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
        assertTrue(urls.contains("http://ant-contrib.sourceforge.net/"));
        assertTrue(urls.contains("http://www.freshports.org/devel/apache-ant/"));
        
        assertEquals(100, results.size());
	}
    
    public void testEntities() throws Exception {
        final GoogleKeysPool pool = new GoogleKeysPool();
        pool.addKeys(new File("keypool"), ".key");

        if (pool.getKeysTotal() == 0) {
            log.error("No available google api keys.");
            return;
        }

        LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new GoogleApiInputComponent(pool);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "Ala ma kota";
        List results = ((ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult()).documents;

        // the results should contain some documents.

        for (Iterator i = results.iterator(); i.hasNext(); ) {
            RawDocument rd = (RawDocument) i.next();

            final String titleSummary = (rd.getTitle() + " " + rd.getSnippet());
            Logger.getRootLogger().info(titleSummary);
            assertTrue(titleSummary.indexOf("&gt;") < 0);
            assertTrue(titleSummary.indexOf("&lt;") < 0);
            assertTrue(titleSummary.indexOf("&amp;") < 0);
        }
    }
}
