
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

package com.dawidweiss.carrot.local.controller.loaders;

import java.io.File;

import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.local.controller.ControllerHelper;
import com.dawidweiss.carrot.local.controller.LoadedProcess;


/**
 *  XML process loader test.
 */
public class XmlProcessLoaderTest extends junit.framework.TestCase {

    public XmlProcessLoaderTest(String s) {
        super(s);
    }

    public void testLoadingProcessesFromDirectory() throws Exception {
        // an easy way to descent to the right package...
        File dir = new File(this.getClass().getName().replace('.', '/')).getParentFile();
        File file = new File(dir, "components");
        File processDir = new File(dir, "processes");

        final LocalController controller = new LocalControllerBase();
        final ControllerHelper cl = new ControllerHelper();

        cl.addComponentFactoriesFromDirectory(controller, file);
        final LoadedProcess process = cl.loadProcess(new File(processDir, "xml-process.xml"));

        assertTrue(process != null);
        assertEquals("xmlprocess", process.getId());
        assertEquals("name", process.getProcess().getName());
        assertEquals("value", process.getAttributes().get("attribute"));
    }
}
