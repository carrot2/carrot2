
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalProcess;
import com.dawidweiss.carrot.core.local.LocalProcessBase;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.local.controller.LoadedProcess;
import com.dawidweiss.carrot.local.controller.ProcessLoader;


/**
 * Loads a {@link LocalProcess} and associated information from  a stream of
 * XML data.  TODO: add code example here.
 * 
 * <p>
 * A list of allowed types for parameters is given below:
 * 
 * <ul>
 * <li>
 * <code>java.lang.Integer</code>
 * </li>
 * <li>
 * <code>java.lang.Long</code>
 * </li>
 * <li>
 * <code>java.lang.Double</code>
 * </li>
 * <li>
 * <code>java.lang.Float</code>
 * </li>
 * <li>
 * <code>java.io.File</code>
 * </li>
 * <li>
 * <code>java.lang.String</code> (default, may be omitted)
 * </li>
 * </ul>
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class XmlProcessLoader implements ProcessLoader {
    /**
     * Loads and initializes a new local process instance. See class
     * documentation for an example of the stream's format.
     *
     * @see com.dawidweiss.carrot.local.controller.ProcessLoader#load(java.io.InputStream)
     */
    public LoadedProcess load(InputStream dataStream) throws IOException {
        // Parse the XML stream
        final SAXReader builder = new SAXReader();
        builder.setValidation(false);

        try {
            final Element root = builder.read(dataStream).getRootElement();
            if (!"local-process".equals(root.getName())) {
                throw new IOException("Malformed stream: root note should " +
                    "be 'local-process'");
            }

            final String id = root.attributeValue("id");
            if (id == null) {
                throw new IOException("Malformed stream: missing id attribute.");
            }

            if (root.elements("input").size() != 1) {
                throw new IOException(
                    "Exactly one 'input' element is expected.");
            }
            final String input = root.element("input").attributeValue("component-key");
            if (input == null) {
                throw new IOException(
                    "Required attribute missing on 'input' element: component-key");
            }

            if (root.elements("output").size() != 1) {
                throw new IOException(
                    "Exactly one 'output' element is expected.");
            }
            final String output = root.element("output").attributeValue("component-key");
            if (output == null) {
                throw new IOException(
                    "Required attribute missing on 'output' element: component-key");
            }

            final ArrayList filters = new ArrayList();
            for (Iterator i = root.elements("filter").iterator(); i.hasNext();) {
                final Element filterElement = (Element) i.next();
                final String filterId = filterElement.attributeValue("component-key");
                if (filterId == null) {
                    throw new IOException(
                        "Required attribute missing on 'filter' element: component-key");
                }
                filters.add(filterId);
            }

            String name = null;
            Element nameElement = root.element("name");
            if (nameElement!= null)
                name = nameElement.getTextTrim();
            
            String description = null;
            Element descriptionElement = root.element("description");
            if (descriptionElement!= null)
                description = descriptionElement.getTextTrim();

            final Map defaultRequestParameters = extractRequestParameters(root.elements("param"));
            final LocalProcessBase process;
            if (defaultRequestParameters != null) {
                process = new LocalProcessBase(input, output,
                        (String[]) filters.toArray(new String[filters.size()]), name, description) {
                            protected void beforeProcessingStartsHook(
                                RequestContext context,
                                LocalComponent[] components) {
                                super.beforeProcessingStartsHook(context,
                                    components);

                                // copy the request parameters.
                                context.getRequestParameters().putAll(defaultRequestParameters);
                            }
                        };
            } else {
                process = new LocalProcessBase(input, output,
                        (String[]) filters.toArray(new String[filters.size()]), name, description);
            }

            // Parse extra process attributes.
            Map attributes = extractRequestParameters(root.elements("attribute"));
            if (attributes == null) {
                attributes = new HashMap();
            }
            return new LoadedProcess(id, process, attributes);
        } catch (DocumentException e) {
            throw new IOException("Malformed stream: XML corrupted: " + e);
        }
    }

    /**
     * Converts XML Elements to a map of name -- value pairs. Value is also
     * converted to one of the allowed types, if such conversion is requested.
     * For the list of allowed types, see the documentation of this class.
     *
     * @param list A list of requested objects.
     *
     * @return Returns a list of name-value pairs.
     */
    private Map extractRequestParameters(List list) throws IOException {
        if (list.size() == 0) {
            return null;
        }

        HashMap map = new HashMap();

        for (Iterator i = list.iterator(); i.hasNext();) {
            Element param = (Element) i.next();
            String key = param.attributeValue("key");
            Object value = param.attributeValue("value");
            String type = param.attributeValue("type");

            if (key == null) {
                throw new IOException(
                    "Required attribute missing on parameter node: key");
            }

            if (value == null) {
                throw new IOException(
                    "Required attribute missing on parameter node: value");
            }

            if ((type != null) && !"java.lang.String".equals(type)) {
                // perform conversion
                if ("java.lang.Integer".equals(type)) {
                    try {
                        value = new Integer((String) value);
                    } catch (NumberFormatException e) {
                        throw new IOException("Value not an integer: " + value);
                    }
                } else if ("java.lang.Double".equals(type)) {
                    try {
                        value = new Double((String) value);
                    } catch (NumberFormatException e) {
                        throw new IOException("Value not a double: " + value);
                    }
                } else if ("java.lang.Float".equals(type)) {
                    try {
                        value = new Float((String) value);
                    } catch (NumberFormatException e) {
                        throw new IOException("Value not a float: " + value);
                    }
                } else if ("java.lang.Long".equals(type)) {
                    try {
                        value = new Long((String) value);
                    } catch (NumberFormatException e) {
                        throw new IOException("Value not a long: " + value);
                    }
                } else if ("java.io.File".equals(type)) {
                    value = new File((String) value);
                }
            }

            map.put(key, value);
        }

        return map;
    }
}
