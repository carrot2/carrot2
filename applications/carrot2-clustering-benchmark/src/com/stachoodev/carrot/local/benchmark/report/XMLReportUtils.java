/*
 * XMLReportUtils.java
 * 
 * Created on 2004-06-30
 */
package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import org.dom4j.*;

/**
 * @author stachoo
 */
public class XMLReportUtils
{
    /**
     * @param list
     * @param listElementName
     * @param entryElementName
     * @return
     */
    public static Element createListElement(List list, String listElementName,
        String entryElementName)
    {
        Element listElement = DocumentHelper.createElement(listElementName);

        for (Iterator iter = list.iterator(); iter.hasNext();)
        {
            Object element = iter.next();
            ElementFactory elementFactory = AllKnownElementFactories
                .getElementFactory(element.getClass());

            if (elementFactory != null)
            {
                listElement.add(elementFactory.createElement(element));
            }
            else
            {
                if (element instanceof Map)
                {
                    listElement.add(createMapElement((Map) element,
                        entryElementName, "entry", "key"));
                }
                else
                {
                    listElement.addElement(entryElementName).addText(
                        element.toString());
                }
            }
        }

        return listElement;
    }

    /**
     * @param map
     * @param mapElementName
     * @param entryElementName
     * @param keyAttributeName
     * @param valueElementName
     * @return
     */
    public static Element createMapElement(Map map, String mapElementName,
        String entryElementName, String keyAttributeName)
    {
        Element mapElement = DocumentHelper.createElement(mapElementName);

        for (Iterator keysIter = map.keySet().iterator(); keysIter.hasNext();)
        {
            Object key = keysIter.next();
            Object value = map.get(key);
            Element entryElement;

            // Add value
            ElementFactory valueElementFactory = AllKnownElementFactories
                .getElementFactory(value.getClass());
            if (valueElementFactory != null)
            {
                entryElement = valueElementFactory.createElement(value);
            }
            else
            {
                entryElement = mapElement.addElement(entryElementName).addText(
                    value.toString());
            }

            // Add key attribute
            entryElement.addAttribute(keyAttributeName, key.toString());
        }

        return mapElement;
    }
}