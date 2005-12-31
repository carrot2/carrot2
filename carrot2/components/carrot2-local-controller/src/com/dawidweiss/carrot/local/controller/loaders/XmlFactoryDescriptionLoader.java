
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.local.controller.ComponentFactoryLoader;
import com.dawidweiss.carrot.local.controller.LoadedComponentFactory;


/**
 * A {@link ComponentFactoryLoader} for creating component factories from an
 * XML description.
 * 
 * <p>
 * The XML description of a factory consists of its identifier, maximum
 * components pool size, and a full name of a Java class of the component to
 * be returned from the factory.
 * </p>
 * 
 * <p>
 * Optionally, the XML may also include a snippet of BeanShell code to
 * initialize every component with. Variable <code>component</code> is
 * registered in the global name space of the BeanShell interpreter to allow
 * access from the scripted code to the newly created component instance.
 * </p>
 * 
 * <p>
 * Another way of passing default parameters to the new component is to use
 * <code>init-properties</code> XML element and specify properties there.
 * </p>
 * 
 * <p>
 * An example component descriptor in XML may look as shown below:
 * <pre>
 * &lt;local-component-factory 
 * 	id="stub-output"
 * 	component-class="com.dawidweiss.carrot.local.controller.StubOutputComponent"&gt;
 * 
 *      &lt;name&gt;Stub Output Component&lt;/name&gt;
 *      &lt;description&gt;A description.&lt;/description&gt;
 *  
 *      &lt;init-beanshell&gt;
 * 		component.setProperty("bshproperty", "value");
 *      &lt;/init-beanshell&gt;
 * 
 *      &lt;init-properties&gt;
 *      	&lt;property name="xmlproperty"&gt;value&lt;/property&gt;
 *      &lt;/init-properties&gt;
 * &lt;/local-component-factory&gt; 
 * </pre>
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class XmlFactoryDescriptionLoader implements ComponentFactoryLoader {
    /**
     * Loads component factory from an XML stream. See class documentation for
     * stream format details.
     *
     * @see com.dawidweiss.carrot.local.controller.ComponentFactoryLoader#load(java.io.InputStream)
     */
    public LoadedComponentFactory load(InputStream dataStream)
        throws IOException, ComponentInitializationException {
        // parse the XML stream
        SAXReader builder = new SAXReader();
        builder.setValidation(false);

        try {
            Element root = builder.read(dataStream).getRootElement();

            if (!"local-component-factory".equals(root.getName())) {
                throw new IOException("Malformed stream: root note should " +
                    "be 'local-component-factory'");
            }

            String id = root.attributeValue("id");

            if (id == null) {
                throw new IOException("Malformed stream: missing id attribute.");
            }

            String componentClass = root.attributeValue("component-class");

            if (componentClass == null) {
                throw new IOException(
                    "Malformed stream: missing 'component-class' attribute");
            }

            String initBeanshell = null;
            Element elem = root.element("init-beanshell");

            if (elem != null) {
                initBeanshell = elem.getText();
            }

            HashMap defaults = new HashMap();
            elem = root.element("init-properties");

            if (elem != null) {
                Iterator i = elem.elements("property").iterator();

                while (i.hasNext()) {
                    Element propertyElement = (Element) i.next();
                    String name = propertyElement.attributeValue("name");
                    String value = propertyElement.getTextTrim();

                    if (name == null) {
                        throw new IOException(
                            "Malformed stream: missing 'name' attribute on 'property'");
                    }

                    if (value == null) {
                        value = "";
                    }

                    defaults.put(name, value);
                }
            }
            
            String name = null;
            Element nameElement = root.element("name");
            if (nameElement != null)
                name = nameElement.getTextTrim();

            String description = null;
            Element descriptionElement = root.element("description");
            if (descriptionElement != null)
                description = descriptionElement.getTextTrim();

            Class componentClazz;

            try {
                componentClazz = Thread.currentThread().getContextClassLoader()
                                       .loadClass(componentClass);
            } catch (ClassNotFoundException e1) {
                throw new IOException(
                    "Could not load component factory because component class does not exist: " +
                    componentClass);
            }

            if (componentClazz.isInterface() ||
                    !Modifier.isPublic(componentClazz.getModifiers())) {
                throw new IOException(
                    "Could not load component factory because class is an interface " +
                    "or is not public: " + componentClass);
            }

            PlainComponentFactory factory = new PlainComponentFactory(componentClazz,
                    initBeanshell, defaults, name, description);

            return new LoadedComponentFactory(id, factory);
        } catch (DocumentException e) {
            throw new IOException("Corrupted or incorrect XML stream: " +
                e.toString());
        }
    }
}
