
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

package org.carrot2.core.controller.loaders;

import java.io.File;

import org.carrot2.core.LocalController;
import org.carrot2.core.LocalControllerBase;
import org.carrot2.core.controller.ControllerHelper;
import org.carrot2.core.controller.LoadedProcess;


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

        cl.addAll(controller, cl.loadComponentFactoriesFromDir(file));
        final LoadedProcess process = cl.loadProcess(new File(processDir, "xml-process.xml"));

        assertTrue(process != null);
        assertEquals("xmlprocess", process.getId());
        assertEquals("name", process.getProcess().getName());
        assertEquals("value", process.getAttributes().get("attribute"));
    }
}
