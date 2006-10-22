
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

package org.carrot2.input.opensearch;

import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayOutputComponent;

/**
 * A test case for OpenSearch input component.
 * 
 * @author Dawid Weiss
 */
public class OpenSearchInputComponentTest extends junit.framework.TestCase {
    private final static Logger log = Logger.getLogger(OpenSearchInputComponentTest.class);

    public OpenSearchInputComponentTest(String s) {
        super(s);
    }

    /**
     * Sends a sample query to icerocket's OpenSearch feed.
     */
    public void testIceRocket() throws Exception {
        final LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new OpenSearchInputComponent("http://blogs.icerocket.com/search?q={searchTerms}&rss=1&os=1&p={startPage}&n={count}");
            }
        };

        final LocalControllerBase controller = setUpController(inputFactory);
        final String query = "blog";
        final long start = System.currentTimeMillis();
        final HashMap params = new HashMap();
        params.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, new Integer(65));
        final List results = ((ArrayOutputComponent.Result) controller.query(
                "testprocess", query, params).getQueryResult()).documents;
        final long end = System.currentTimeMillis();
        log.info("Open Search query time: " + (end - start) + " ms.");

        // the results should contain some documents.
        assertEquals("Results acquired > 0?: " + results.size(), 65, results.size());
        
        // Check for uniqueness of the ids
        Set idSet = new HashSet();
        for (Iterator it = results.iterator(); it.hasNext();)
        {
            RawDocument document = (RawDocument) it.next();
            assertFalse("Unique document id", idSet.contains(document.getId()));
            idSet.add(document.getId());
        }
    }

	private LocalControllerBase setUpController(LocalComponentFactory inputFactory) throws Exception {
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
}
