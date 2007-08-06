
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.controller.loaders;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.carrot2.core.*;
import org.carrot2.core.controller.*;


/**
 *  Beanshell component factory loader test.
 */
public class BeanShellFactoryDescriptionLoaderTest
    extends TestCase {

    private LocalControllerBase controller = new LocalControllerBase();
    private ControllerHelper cl = new ControllerHelper();

    public BeanShellFactoryDescriptionLoaderTest(String s) {
        super(s);
    }

    public void testLoadingComponentFromBSHStream() throws Exception {
        addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output-bsh"));
    }

    public void testPropertiesHaveBeenSetInBeanShellLoader()
        throws Exception {
        addComponentFactory(controller, "bsh",
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
        addComponentFactory(controller, "bsh",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.bsh"));
        assertTrue(controller.isComponentFactoryAvailable("stub-output-bsh"));
    }
    
    private void addComponentFactory(LocalControllerBase controller, String loaderId, InputStream stream) 
        throws LoaderExtensionUnknownException, IOException, ComponentInitializationException, DuplicatedKeyException
    {
        LoadedComponentFactory lcf = this.cl.loadComponentFactory(loaderId, stream);
        controller.addLocalComponentFactory(lcf.getId(), lcf.getFactory());
    }
}
