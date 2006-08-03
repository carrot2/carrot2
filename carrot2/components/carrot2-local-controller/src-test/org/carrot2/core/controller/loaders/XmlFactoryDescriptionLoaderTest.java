
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

import java.io.IOException;
import java.io.InputStream;

import org.carrot2.core.*;
import org.carrot2.core.controller.*;


/**
 *  XML component factory loader test.
 */
public class XmlFactoryDescriptionLoaderTest extends junit.framework.TestCase {
    private LocalControllerBase controller = new LocalControllerBase();
    private ControllerHelper cl = new ControllerHelper();

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
        addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output"));
    }

    public void testPropertiesHaveBeenSetInXmlLoader()
        throws Exception {
        addComponentFactory(controller, "xml",
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
        addComponentFactory(controller, "xml",
            this.getClass().getResourceAsStream("components/StubOutputComponentDescriptor.xml"));

        assertTrue(controller.isComponentFactoryAvailable("stub-output"));
    }
    
    private void addComponentFactory(LocalControllerBase controller, String loaderId, InputStream stream) 
        throws LoaderExtensionUnknownException, IOException, ComponentInitializationException, DuplicatedKeyException
    {
        LoadedComponentFactory lcf = this.cl.loadComponentFactory(loaderId, stream);
        controller.addLocalComponentFactory(lcf.getId(), lcf.getFactory());
    }
}
