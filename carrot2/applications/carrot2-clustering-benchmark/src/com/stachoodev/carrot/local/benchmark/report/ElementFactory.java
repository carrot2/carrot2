/*
 * ElementFactory.java
 * 
 * Created on 2004-06-30
 */
package com.stachoodev.carrot.local.benchmark.report;

import org.dom4j.*;

/**
 * @author stachoo
 */
public interface ElementFactory
{
    /**
     * @param object
     * @return
     */
    public Element createElement(Object object);
}
