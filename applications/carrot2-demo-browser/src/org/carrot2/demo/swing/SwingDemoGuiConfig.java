
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

package org.carrot2.demo.swing;

import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * Contains configuration information for the GUI browser.
 * 
 * @author Stanislaw Osinski
 */
public class SwingDemoGuiConfig
{
    private final Logger logger = Logger.getLogger(SwingDemoGuiConfig.class);

    public int mainWindowWidth = 800;
    public int mainWindowHeight = 600;

    public String [] requestedResultCounts = new String []
    {
        "50", "100", "150", "200", "400"
    };
    public String selectedResultCount = "100";

    /**
     * Creates a default instance of the config.
     */
    public SwingDemoGuiConfig()
    {
    }

    /**
     * Reads the config from an XML file specified by the resource path.
     * 
     * @param resourcePath
     */
    public SwingDemoGuiConfig(String resourcePath)
    {
        SAXReader reader = new SAXReader();
        Document document;
        try
        {
            final InputStream resourceAsStream = SwingDemoGuiConfig.class
                .getResourceAsStream(resourcePath);
            if (resourceAsStream == null)
            {
                logger.warn("Cannot load configuration from: " + resourcePath);
                return;
            }
            document = reader.read(resourceAsStream);
        }
        catch (DocumentException e)
        {
            logger.warn("Cannot load configuration from: " + resourcePath, e);
            return;
        }

        final Element rootElement = document.getRootElement();

        loadMainWindowSize(rootElement);
        loadRequestedResultCounrs(rootElement);
    }

    /**
     * @param rootElement
     */
    private void loadRequestedResultCounrs(Element rootElement)
    {
        Element resultCountsElement = rootElement.element("requested-results");
        if (resultCountsElement != null)
        {
            List resultsElements = resultCountsElement.elements("results");
            List results = new ArrayList();
            for (Iterator it = resultsElements.iterator(); it.hasNext();)
            {
                Element element = (Element) it.next();
                results.add(element.getTextTrim());
                if (element.attributeValue("selected") != null)
                {
                    selectedResultCount = element.getTextTrim();
                }
            }

            requestedResultCounts = (String []) results
                .toArray(new String [results.size()]);
        }
    }

    /**
     * @param rootElement
     */
    private void loadMainWindowSize(final Element rootElement)
    {
        Element mainWindowSizeElement = rootElement.element("main-window-size");
        if (mainWindowSizeElement != null)
        {
            Element mainWindowWidthElement = mainWindowSizeElement
                .element("width");
            if (mainWindowWidthElement != null)
            {
                try
                {
                    mainWindowWidth = Integer.parseInt(mainWindowWidthElement
                        .getText());
                }
                catch (Exception e)
                {
                    logger.warn("Failed to parse main window width", e);
                }
            }

            Element mainWindowHeightElement = mainWindowSizeElement
                .element("height");
            if (mainWindowHeightElement != null)
            {
                try
                {
                    mainWindowHeight = Integer.parseInt(mainWindowHeightElement
                        .getText());
                }
                catch (Exception e)
                {
                    logger.warn("Failed to parse main height width", e);
                }
            }
        }
    }
}
