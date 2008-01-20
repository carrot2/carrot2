
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

package org.carrot2.util;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

public class Dom4jUtils {
    public static void removeChildren(Element element, String childrenNames) {
        List list = element.elements(childrenNames);
        for (Iterator i = list.iterator(); i.hasNext();) {
            element.remove(((Element) i.next())); 
        }
    }
}
