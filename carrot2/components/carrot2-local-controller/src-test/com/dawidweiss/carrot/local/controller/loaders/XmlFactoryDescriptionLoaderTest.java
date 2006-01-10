
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

package com.dawidweiss.carrot.local.controller.loaders;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.local.controller.ControllerHelper;
import com.dawidweiss.carrot.local.controller.StubOutputComponent;


/**
 *  XML component factory loader test.
 */
public class XmlFactoryDescriptionLoaderTest extends junit.framework.TestCase {
    /**
     * Creates a new XmlFactoryDescriptionLoaderTest object.
     */
    public XmlFactoryDescriptionLoaderTest() {
        super();
    }

    /**
     * Creates a new XmlFactoryDescriptionLoaderTest object.
     */
    public XmlFactoryDescriptionLoaderTest(String s) {
        super(s);
    }

    public void testLoadingComponentFromXMLStream() throws Exception {
        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output"));
    }

    public void testPropertiesHaveBeenSetInXmlLoader()
        throws Exception {
        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output"));

        LocalComponent component = controller.borrowComponent("stub-output");
        assertNotNull(component);
        assertTrue(component instanceof StubOutputComponent);
        assertEquals("value",
            ((StubOutputComponent) component).getProperty("bshproperty"));
        assertEquals("value",
            ((StubOutputComponent) component).getProperty("xmlproperty"));
    }
    
    public void testNameHasBeenSetInXmlLoader()
        throws Exception {
        LocalControllerBase controller = new LocalControllerBase();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output"));
    }
}
