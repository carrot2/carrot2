/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import org.dom4j.*;

import com.dawidweiss.carrot.util.common.*;

/**
 * Various utility methods for XML reports.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class XMLReportUtils
{
    /**
     * Creates an element for a {@link List}
     * 
     * @param list data for the element
     * @param listElementName name for the list element
     * @param entryElementName name for a single list element. The name will
     *            only be used if no {@link ElementFactory}is available for the
     *            a particular list element.
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
     * Creates an element for a {@link Map}.
     * 
     * @param map data for the element
     * @param mapElementName name for the map element
     * @param entryElementName name for a single map entry element. The name
     *            will only be used if no {@link ElementFactory}is available
     *            for the a particular map entry.
     * @param keyAttributeName name for a single map key attribute
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
                // Try primitives
                if (value instanceof Double)
                {
                    entryElement = mapElement.addElement(entryElementName)
                        .addText(StringUtils.toString((Double) value, "#.##"));
                }
                else
                {
                    entryElement = mapElement.addElement(entryElementName)
                        .addText(value.toString());
                }
            }

            // Add key attribute
            entryElement.addAttribute(keyAttributeName, key.toString());
        }

        return mapElement;
    }
}