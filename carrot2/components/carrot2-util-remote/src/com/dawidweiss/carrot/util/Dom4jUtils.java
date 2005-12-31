package com.dawidweiss.carrot.util;

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
