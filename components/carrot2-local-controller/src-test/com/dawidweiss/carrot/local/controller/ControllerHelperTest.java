
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
import java.util.HashMap;
import java.util.List;

import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.ProcessingResult;


/**
 * Tests of the controller helper (utilities for loading component factories
 * and processes from a serialized form).
 */
public class ControllerHelperTest extends junit.framework.TestCase {

    public ControllerHelperTest(String s) {
        super(s);
    }

    public void testLoadingComponentFromXMLStream() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller,
            ControllerHelper.EXT_COMPONENT_FACTORY_LOADER_XML,
            this.getClass().getResourceAsStream("loaders/components/StubOutputComponentDescriptor.xml"));
        assertTrue(controller.isComponentFactoryAvailable("stub-output"));
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

        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, file);
        assertTrue(controller.isComponentFactoryAvailable("stub-output"));
    }

    public void testLoadingComponentFromDirectory() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");

        if (!file.exists()) {
            throw new RuntimeException("Cannot find the test dir: " +
                file.getAbsolutePath() +
                ". Run tests in test classes directory.");
        }

        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactoriesFromDirectory(controller, file);
        assertTrue(controller.isComponentFactoryAvailable("stub-output"));
        assertTrue(controller.isComponentFactoryAvailable("stub-output-bsh"));
    }

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

        LocalControllerBase controller = new LocalControllerBase();
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

    public void testLoadingProcessesFromStream() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");

        LocalController controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        LoadedProcess loadedProcess = cl.loadProcess(ControllerHelper.EXT_PROCESS_LOADER_XML,
            this.getClass().getResourceAsStream("loaders/processes/xml-process.xml"));
        controller.addProcess(loadedProcess.getId(), loadedProcess.getProcess());

        assertTrue(controller.getProcessIds().contains("xmlprocess"));

        ProcessingResult result = controller.query("xmlprocess", "query", new HashMap());
        assertEquals("i:begin,f:begin,o:begin,i:end,f:end,o:end,",
            result.getQueryResult().toString());
    }

    public void testLoadingProcessesFromFile() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");
        File processFile = new File(dir,
                "loaders" + File.separator + "processes" + File.separator +
                "xml-process.xml");

        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        final LoadedProcess loadedProcess = cl.loadProcess(processFile);
        controller.addProcess(loadedProcess.getId(), loadedProcess.getProcess());

        assertTrue(controller.getProcessIds().contains("xmlprocess"));
    }

    public void testLoadingProcessesFromDirectory() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");
        File processDir = new File(dir, "loaders" + File.separator +
                "processes");

        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        cl.addProcessesFromDirectory(controller, processDir);

        assertTrue(controller.getProcessIds().contains("xmlprocess"));
    }

    public void testLoadingProcessesFromDirectoryWithFilter()
        throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "loaders" + File.separator + "components");
        File processDir = new File(dir, "loaders" + File.separator +
                "processes");

        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        cl.addProcessesFromDirectory(controller, processDir,
            new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".xml");
                }
            });

        List processes = controller.getProcessIds();
        assertTrue(processes.contains("xmlprocess"));
        assertFalse(processes.contains("bshprocess"));
    }
}
