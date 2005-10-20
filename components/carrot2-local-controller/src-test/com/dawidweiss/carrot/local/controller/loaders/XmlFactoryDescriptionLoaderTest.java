
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

    /*
     */
    public void testLoadingComponentFromXMLStream() throws Exception {
        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output"));
    }

    /*
     */
    public void testPropertiesHaveBeenSetInXmlLoader()
        throws Exception {
        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output"));

        LocalComponent component = controller.borrowComponent("stub-output");
        assertNotNull(component);
        assertTrue(component instanceof StubOutputComponent);
        assertEquals("value",
            ((StubOutputComponent) component).getProperty("bshproperty"));
        assertEquals("value",
            ((StubOutputComponent) component).getProperty("xmlproperty"));
    }
    
    /*
     */
    public void testNameHasBeenSetInXmlLoader()
        throws Exception {
        LocalController controller = new LocalController();
        ControllerHelper cl = new ControllerHelper();
        cl.addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(Arrays.asList(controller.getComponentFactoryNames())
                         .contains("stub-output"));
    }
}
