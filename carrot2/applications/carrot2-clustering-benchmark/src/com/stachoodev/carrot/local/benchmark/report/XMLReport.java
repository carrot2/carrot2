/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.local.benchmark.report;

import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

/**
 * A utility class for generating simple XML reports consisting mainlyg of dumps
 * of different Java objects.
 * 
 * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory
 * @see com.stachoodev.carrot.local.benchmark.report.AllKnownElementFactories
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class XMLReport
{
    /** Current XML document */
    private Document document;

    /** Current XML root element */
    private Element root;

    /**
     * Creates a new report with given root element name.
     * 
     * @param rootElementName
     */
    public XMLReport(String rootElementName)
    {
        document = DocumentHelper.createDocument();
        root = document.addElement(rootElementName);
    }

    /**
     * Serializes this report to given {@link OutputStream}. It is the
     * responsibility of the caller to close the output stream.
     * 
     * @param out where to serialize the report
     * @throws IOException
     */
    public void serialize(OutputStream out) throws IOException
    {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(document);
    }

    /**
     * Serializes this report to given <code>file</code>.
     * 
     * @param file
     * @throws IOException
     */
    public void serialize(File file) throws IOException
    {
        OutputStream out = new FileOutputStream(file);
        serialize(out);
        out.close();
    }

    /**
     * Adds a {@link Map}to this report.
     * 
     * @param map the map to be added
     * @param description description of the map, or <code>null</code>
     * @param elementName name of the map's tag
     * @param keyAttributeName name of the attribute representing map's keys
     * @param valueElementName name of the element representing map's values
     */
    public void addMap(Map map, String description, String mapElementName,
        String entryElementName, String keyAttributeName)
    {
        Element mapElement = XMLReportUtils.createMapElement(map,
            mapElementName, entryElementName, keyAttributeName);

        if (description != null)
        {
            mapElement.addElement("description").addText(description);
        }

        root.add(mapElement);
    }

    /**
     * Adds an object to the report.
     * 
     * TODO: implement this
     * 
     * @param object object ot be added
     * @param description description of the object or <code>null</code>
     */
    public void addObject(Object object, String description)
    {
        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Adds a {@link List}to this report.
     * 
     * @param list the list to be added
     * @param description description od the list or <code>null</code>
     * @param listElementName name of the element representing the whole list
     */
    public void addList(List list, String description, String listElementName,
        String listEntryName)
    {
        Element listElement = XMLReportUtils.createListElement(list,
            listElementName, listEntryName);

        if (description != null)
        {
            listElement.addElement("description").addText(description);
        }

        root.add(listElement);
    }
}