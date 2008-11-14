
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.xml.google;

import java.util.HashMap;

import junit.framework.TestCase;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;
import org.carrot2.core.LocalControllerBase;
import org.carrot2.core.LocalProcessBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.impl.ArrayOutputComponent;

/**
 * Tests {@link GoogleApplianceInputComponent}.
 */
public class GoogleApplianceInputComponentTest extends TestCase {

    public GoogleApplianceInputComponentTest(String s) {
        super(s);
    }
    
	protected LocalControllerBase setUpController() throws Exception {
		LocalControllerBase controller;
		
        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new ArrayOutputComponent();
            }
        };
        
        LocalComponentFactory inputFactory = new LocalComponentFactory()
        {
            public LocalComponent getInstance()
            {
                return new GoogleApplianceInputComponent();
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
	
	/*
	 * 
	 */
	public void testNoParamsInContext() throws Exception {
        LocalControllerBase controller = setUpController();

        String query = "";
        HashMap params = new HashMap();
        try {
            controller.query("testprocess", query, params).getQueryResult();
	        fail();
        } catch (ProcessingException e) {
            // ok, this is expected.
        }
	}

	/*
	 * 
	 */
	public void testAstronautFile() throws Exception {
        LocalControllerBase controller = setUpController();

        final String query = "astronaut";
        final HashMap params = new HashMap();

        params.put(GoogleApplianceInputComponent.PARAM_GOOGLE_APPLIANCE_SERVICE_URL_BASE, 
            this.getClass().getResource("astronaut.xml").toExternalForm());

        final ArrayOutputComponent.Result queryResult = 
            (ArrayOutputComponent.Result) controller.query("testprocess", query, params).getQueryResult();
        assertEquals(100, queryResult.documents.size());
	}
}
