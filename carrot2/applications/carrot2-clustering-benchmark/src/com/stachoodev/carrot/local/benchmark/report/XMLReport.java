/*
 * XMLReport.java
 * 
 * Created on 2004-06-30
 */
package com.stachoodev.carrot.local.benchmark.report;

import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

/**
 * @author stachoo
 */
public class XMLReport
{
    /** Current XML document */
    private Document document;

    /** Current XML root element */
    private Element root;

    /**
     * @param rootElementName
     */
    public XMLReport(String rootElementName)
    {
        document = DocumentHelper.createDocument();
        root = document.addElement(rootElementName);
    }

    /**
     * It is the responsibility of the caller to close the output stream.
     * 
     * @param out
     * @throws IOException
     */
    public void serialize(OutputStream out) throws IOException
    {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(document);
    }

    /**
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
     * @param map
     * @param description
     * @param elementName
     * @param keyElementName
     * @param valueElementName
     */
    public void addMap(Map map, String description, String mapElementName,
        String entryElementName, String keyElementName)
    {
        Element mapElement = XMLReportUtils.createMapElement(map,
            mapElementName, entryElementName, keyElementName);

        if (description != null)
        {
            mapElement.addElement("description").addText(description);
        }

        root.add(mapElement);
    }

    /**
     * @param object
     * @param description
     */
    public void addObject(Object object, String description)
    {

    }

    /**
     * @param list
     * @param description
     * @param listElementName
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