
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.local.controller;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalFilterComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalProcessBase;

import java.util.*;
import java.util.List;
import java.util.Set;


/**
 * Local controller tests.
 */
public class LocalControllerTest extends junit.framework.TestCase {
    /*
     */
    public LocalControllerTest() {
        super();
    }

    /*
     */
    public LocalControllerTest(String s) {
        super(s);
    }

    /*
     */
    public void testCorrectComponentFactoryAddition() throws Exception {
        LocalController controller = new LocalController();

        StubInputComponentFactory factory = new StubInputComponentFactory();
        controller.addComponentFactory("key", factory, 5);
        assertTrue(controller.isComponentFactoryAvailable("key"));

        LocalComponent instance = null;

        if ((instance = controller.borrowComponent("key")) != null) {
            controller.returnComponent("key", instance);
        }

        assertNotNull(instance);
    }

    /*
     */
    public void testDuplicatedKeyInComponentFactoryAddition()
        throws Exception {
        LocalController controller = new LocalController();

        StubInputComponentFactory factory = new StubInputComponentFactory();
        controller.addComponentFactory("key", factory, 5);

        try {
            controller.addComponentFactory("key", factory, 5);
            fail("Should have failed.");
        } catch (DuplicatedKeyException e) {
            // this is expected behavior.
        }
    }

    /*
     */
    public void testContextPassedOnInitialize() throws Exception {
        LocalController controller = new LocalController();

        StubInputComponentFactory factory = new StubInputComponentFactory();
        controller.addComponentFactory("key", factory, 5);

        List l = factory.getCreatedInstances();

        for (int i = 0; i < l.size(); i++) {
            StubInputComponent component = (StubInputComponent) l.get(i);
            assertNotNull(component.getLocalControllerContext());
        }
    }

    /*
     */
    public void testProcessAddition() throws Exception {
        LocalController controller = new LocalController();

        controller.addComponentFactory("input",
            new StubInputComponentFactory(), 5);
        controller.addComponentFactory("filter",
            new StubFilterComponentFactory(), 5);
        controller.addComponentFactory("output",
            new StubOutputComponentFactory(), 5);

        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter");
        process.addFilter("filter");

        controller.addProcess("process", process);
    }

    /*
     */
    public void testProcessAdditionAndQuerying() throws Exception {
        LocalController controller = new LocalController();

        controller.addComponentFactory("input",
            new StubInputComponentFactory(), 5);
        controller.addComponentFactory("filter1",
            new StubFilterComponentFactory("f1"), 5);
        controller.addComponentFactory("filter2",
            new StubFilterComponentFactory("f2"), 5);
        controller.addComponentFactory("output",
            new StubOutputComponentFactory(), 5);

        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        controller.addProcess("process", process);

        Object result = controller.query("process", "query",
                java.util.Collections.EMPTY_MAP);

        // check the expected output: every component
        // simply adds a single letter and a comma.
        System.out.println(result.toString());

        assertEquals("i:begin,f1:begin,f2:begin,o:begin,i:end,f1:end,f2:end,o:end,",
            result.toString());
    }

    /*
     */
    public void testVerifierIncompatibleComponents() throws Exception {
        LocalController controller = new LocalController();

        Set a = new HashSet();
        Set b = new HashSet();
        a.add("capabilityA");
        b.add("capabilityB");

        controller.addComponentFactory("input",
            new StubInputComponentFactory(), 5);
        controller.addComponentFactory("filter1",
            new StubFilterComponentFactory("f1", a, Collections.EMPTY_SET, b), 5);
        controller.addComponentFactory("filter2",
            new StubFilterComponentFactory("f2", Collections.EMPTY_SET, a,
                Collections.EMPTY_SET), 5);
        controller.addComponentFactory("output",
            new StubOutputComponentFactory(), 5);

        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        try {
            controller.addProcess("process", process);
            fail("Components incompatibility not detected.");
        } catch (Exception e) {
            // ok, this is expected behavior.
        }
    }

    /*
     */
    public void testVerifierCompatibleComponents() throws Exception {
        LocalController controller = new LocalController();

        Set a = new HashSet();
        Set b = new HashSet();
        a.add("capabilityA");
        b.add("capabilityB");

        controller.addComponentFactory("input",
            new StubInputComponentFactory(), 5);
        controller.addComponentFactory("filter1",
            new StubFilterComponentFactory("f1", a, Collections.EMPTY_SET, b), 5);
        controller.addComponentFactory("filter2",
            new StubFilterComponentFactory("f2", b, a, Collections.EMPTY_SET), 5);
        controller.addComponentFactory("output",
            new StubOutputComponentFactory(), 5);

        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        controller.addProcess("process", process);
    }

    /*
     */
    public void testSetNextInvocationContract()
        throws DuplicatedKeyException, Exception {
        LocalController controller = new LocalController();

        Set a = new HashSet();
        Set b = new HashSet();
        a.add("capabilityA");
        b.add("capabilityB");

        controller.addComponentFactory("input",
            new StubInputComponentFactory(), 5);
        controller.addComponentFactory("filter1",
            new StubFilterComponentFactory("f1", a, Collections.EMPTY_SET, b), 5);
        controller.addComponentFactory("filter2",
            new StubFilterComponentFactory("f2", b, a, Collections.EMPTY_SET), 5);
        controller.addComponentFactory("output",
            new StubOutputComponentFactory(), 5);

        final boolean[] states = new boolean[2];

        LocalProcessBase process = new LocalProcessBase() {
                protected void beforeProcessingStartsHook(
                    LocalComponent[] components) {
                }

                protected void afterProcessingStartedHook(
                    LocalComponent[] components) {
                    try {
                        ((LocalInputComponent) components[0]).setNext(components[1]);
                    } catch (IllegalStateException e) {
                        // component should have returned illegal state here.
                        states[0] = true;
                    }

                    try {
                        ((LocalFilterComponent) components[1]).setNext(components[2]);
                    } catch (IllegalStateException e) {
                        // component should have returned illegal state here.
                        states[1] = true;
                    }
                }
            };

        process.setInput("input");
        process.setOutput("output");
        process.addFilter("filter1");
        process.addFilter("filter2");

        controller.addProcess("process", process);

        assertFalse("Stub Input component contract on setNext() not respected.",
            states[0]);
        assertFalse("Stub Filter component contract on setNext() not respected.",
            states[1]);
    }
}
