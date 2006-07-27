
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

import java.util.Arrays;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalControllerBase;
import org.carrot2.core.controller.ControllerHelper;
import org.carrot2.core.controller.StubOutputComponent;


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
