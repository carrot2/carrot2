
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

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.local.controller.ControllerHelper;
import com.dawidweiss.carrot.local.controller.LocalController;
import com.dawidweiss.carrot.local.controller.StubOutputComponent;

import java.util.Arrays;


/**
 *  Beanshell component factory loader test.
 */
public class BeanShellFactoryDescriptionLoaderTest
    extends junit.framework.TestCase {
    /**
     * Creates a new BeanShellFactoryDescriptionLoaderTest object.
     */
    public BeanShellFactoryDescriptionLoaderTest() {
        super();
    }

    /**
     * Creates a new BeanShellFactoryDescriptionLoaderTest object.
     */
    public BeanShellFactoryDescriptionLoaderTest(String s) {
        super(s);
    }

    /*
     */
    public void testLoadingComponentFromBSHStream() throws Exception {
        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));

        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output-bsh"));
    }

    /*
     */
    public void testPropertiesHaveBeenSetInBeanShellLoader()
        throws Exception {
        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));

        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output-bsh"));

        LocalComponent component = controller.borrowComponent("stub-output-bsh");
        assertNotNull(component);
        assertTrue(component instanceof StubOutputComponent);
        assertEquals("value",
            ((StubOutputComponent) component).getProperty("property"));
    }

    /*
     */
    public void testNameAndDescriptionHasBeenSetInBeanShellLoader()
        throws Exception {
        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));

        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output-bsh"));

        LocalComponentFactory factory = controller.getFactory("stub-output-bsh");
        assertEquals("name", factory.getName());
        assertEquals("description", factory.getDescription());
    }
}
