
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.local.controller.loaders;

import java.util.Arrays;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.local.controller.ControllerHelper;
import com.dawidweiss.carrot.local.controller.StubOutputComponent;


/**
 *  Beanshell component factory loader test.
 */
public class BeanShellFactoryDescriptionLoaderTest
    extends junit.framework.TestCase {

    public BeanShellFactoryDescriptionLoaderTest(String s) {
        super(s);
    }

    public void testLoadingComponentFromBSHStream() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output-bsh"));
    }

    public void testPropertiesHaveBeenSetInBeanShellLoader()
        throws Exception {
        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output-bsh"));

        LocalComponent component = controller.borrowComponent("stub-output-bsh");
        assertNotNull(component);
        assertTrue(component instanceof StubOutputComponent);
        assertEquals("value",
            ((StubOutputComponent) component).getProperty("property"));
    }

    public void testNameAndDescriptionHasBeenSetInBeanShellLoader()
        throws Exception {
        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));
        assertTrue(controller.isComponentFactoryAvailable("stub-output-bsh"));
    }
}
