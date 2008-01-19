
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.carrot2.demo.swing.SwingDemoGui;
import org.carrot2.demo.swing.SwingUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * Carrot2 demo in Swing.
 * 
 * @author Dawid Weiss
 */
public class DemoSwing implements SplashDelegate {
    
    /**
     * Demo Entry point.
     */
    public static void main(String[] args) {
        new DemoSwing().main(args, null);
    }

    /**
     * 
     */
    public void main(String [] args, final JWindow splash)
    {
        final DemoContext carrotDemo;

        if (args.length > 0) {
            if ("--resource".equals(args[0])) {
                // Perform resource load.
                try {
                    carrotDemo = initFromResource();
                } catch (IOException e) {
                    SwingUtils.showExceptionDialog(null, "Initialization error.", e);
                    return;
                }
            } else {
                throw new RuntimeException("Unknown parameter: " + args[0]);
            }
        } else {
            carrotDemo = new DemoContext();
        }

        final SwingDemoGui demoGui = new SwingDemoGui(carrotDemo, getMainFrameTitle());
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run()
                {
                    demoGui.display(splash);
                }
            });
    }

    /**
     * 
     */
    protected String getMainFrameTitle()
    {
        return "Carrot2 Demo";
    }
    
    /**
     * 
     */
    private static DemoContext initFromResource() throws IOException {
        final ArrayList components = new ArrayList();
        final ArrayList processes = new ArrayList();

        final InputStream definitions = DemoSwing.class.getResourceAsStream("/demo-resources.xml");
        if (definitions == null) {
            throw new IOException("No resource file specification.");
        }
        final SAXReader reader = new SAXReader(false);
        try {
            final Element root = reader.read(definitions).getRootElement();
            for (Iterator i = root.elements().iterator(); i.hasNext();) {
                final Element elem = (Element) i.next();
                final URL url;
                
                final Attribute resourceAttrib = elem.attribute("resource");
                if (resourceAttrib != null) {
                    url = DemoSwing.class.getResource(resourceAttrib.getValue());
                    if (url == null)
                        throw new IOException("Resource not found: "
                                + resourceAttrib.getValue());
                } else if (elem.attribute("url") != null) {
                    url = new URL(elem.attributeValue("url"));
                } else {
                    throw new DocumentException("Missing resource or url attribute.");
                }

                if ("component".equals(elem.getName())) {
                    components.add(url);
                } else if ("process".equals(elem.getName())) {
                    processes.add(url);
                } else {
                    throw new DocumentException("Unknown element: " + elem.getName());
                }
            }
        } catch (DocumentException e) {
            throw new IOException("Demo resources XML malformed: " + e);
        }

        return new DemoContext(
                (URL[]) components.toArray(new URL [components.size()]),
                (URL[]) processes.toArray(new URL [processes.size()]));
    }
}
