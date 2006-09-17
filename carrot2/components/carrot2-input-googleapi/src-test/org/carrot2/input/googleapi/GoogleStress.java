
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

import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.impl.ArrayOutputComponent;

/**
 * Stress test for Google keys.
 * 
 * @author Dawid Weiss
 */
public class GoogleStress extends TestCase {
    private final static Logger log = Logger.getLogger(GoogleStress.class);
    
	public GoogleStress(String tname) {
		super(tname);
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

        // pool.addKey("", "key1");

        LocalComponentFactory inputFactory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new GoogleApiInputComponent(pool);
            }
        };

        LocalControllerBase controller = setUpController(inputFactory);
        String query = "dawid weiss";

        int count = 0;
        while (true) {
            final long start = System.currentTimeMillis();
            ArrayOutputComponent.Result all = (ArrayOutputComponent.Result) controller.query("testprocess", query, new HashMap()).getQueryResult();
            final long end = System.currentTimeMillis();
            log.info("GoogleAPI query time: " + (end - start) + " ms.");
            log.info("GoogleAPI query: " + count);
            count++;
        }
    }
}
