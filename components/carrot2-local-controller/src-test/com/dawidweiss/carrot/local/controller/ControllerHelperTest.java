
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

import java.io.File;
import java.io.FileFilter;

import java.util.Arrays;


/**
 * Tests of the controller helper (utilities for loading component factories
 * and processes from a serialized form).
 */
public class ControllerHelperTest extends junit.framework.TestCase {
    /*
     */
    public ControllerHelperTest() {
        super();
    }

    /*
     */
    public ControllerHelperTest(String s) {
        super(s);
    }

    /*
     */
    public void testLoadingComponentFromXMLStream() throws Exception {
        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller,
            ControllerHelper.EXT_COMPONENT_FACTORY_LOADER_XML,
            this.getClass().getResourceAsStream("loaders/components/StubOutputComponentDescriptor.xml"));

        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output"));
    }

    /*
     */
    public void testLoadingComponentFromFile() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir,
                "loaders" + File.separator + "components" + File.separator +
                "StubOutputComponentDescriptor.xml");

        if (!file.exists()) {
            throw new RuntimeException("Cannot find the test file: " +
                file.getAbsolutePath() +
                ". Run tests in test classes directory.");
        }

        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, file);
        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output"));
    }

    /*
     */
    public void testLoadingComponentFromDirectory() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");

        if (!file.exists()) {
            throw new RuntimeException("Cannot find the test dir: " +
                file.getAbsolutePath() +
                ". Run tests in test classes directory.");
        }

        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactoriesFromDirectory(controller, file);
        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output"));
        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output-bsh"));
    }

    /*
     */
    public void testLoadingComponentFromDirectoryWithFilter()
        throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");

        if (!file.exists()) {
            throw new RuntimeException("Cannot find the test dir: " +
                file.getAbsolutePath() +
                ". Run tests in test classes directory.");
        }

        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactoriesFromDirectory(controller, file,
            new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".xml");
                }
            });
        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output"));
        assertFalse(Arrays.asList(controller.getComponentFactoryNames())
                          .contains("stub-output-bsh"));
    }

    /*
     */
    public void testLoadingProcessesFromStream() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");

        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        cl.addProcess(controller, ControllerHelper.EXT_PROCESS_LOADER_XML,
            this.getClass().getResourceAsStream("loaders/processes/xml-process.xml"));

        assertTrue(Arrays.asList(controller.getProcessNames()).contains("xmlprocess"));

        Object result = controller.query("xmlprocess", "query",
                java.util.Collections.EMPTY_MAP);
        assertEquals("i:begin,f:begin,o:begin,i:end,f:end,o:end,",
            result.toString());
    }

    /*
     */
    public void testLoadingProcessesFromFile() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");
        File process = new File(dir,
                "loaders" + File.separator + "processes" + File.separator +
                "xml-process.xml");

        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        cl.addProcess(controller, process);

        assertTrue(Arrays.asList(controller.getProcessNames()).contains("xmlprocess"));
    }

    /*
     */
    public void testLoadingProcessesFromDirectory() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");
        File processDir = new File(dir, "loaders" + File.separator +
                "processes");

        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        cl.addProcessesFromDirectory(controller, processDir);

        assertTrue(Arrays.asList(controller.getProcessNames()).contains("xmlprocess"));
    }

    /*
     */
    public void testLoadingProcessesFromDirectoryWithFilter()
        throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");
        File processDir = new File(dir, "loaders" + File.separator +
                "processes");

        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        cl.addProcessesFromDirectory(controller, processDir,
            new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".xml");
                }
            });

        assertTrue(Arrays.asList(controller.getProcessNames()).contains("xmlprocess"));
        assertFalse(Arrays.asList(controller.getProcessNames()).contains("bshprocess"));
    }
}
