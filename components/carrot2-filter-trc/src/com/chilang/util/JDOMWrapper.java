/**
 * 
 * @author chilang
 * Created 2003-12-29, 20:34:10.
 */
package com.chilang.util;

import org.jdom.Element;


/**
 * Wrapper that turn object into JDOM Element
 */
public interface JDOMWrapper {


    /**
     * Return wrapped object as an Element
     * @return
     */
    public Element asElement();

}
