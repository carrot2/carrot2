

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2.process;


import java.util.*;


/**
 * Stores the result or a list of processing exceptions.
 */
public class ProcessingResultHolder
{
    private boolean erraneous;
    private List exceptions;
    private Map attributes;

    /**
     * Construct a new empty ResultHolder object.
     */
    public ProcessingResultHolder()
    {
        exceptions = new LinkedList();
        attributes = new TreeMap();
    }

    /**
     * If set to true, indicates the processing was successfull.
     */
    public boolean isErraneous()
    {
        return erraneous;
    }


    /**
     * Set to indicate whether processing was erranous or not.
     */
    public void setErraneous(boolean erraneous)
    {
        this.erraneous = erraneous;
    }


    /**
     * Returns a Map of processing attributes, which may include information about speed of
     * calculation, memory utilization and anything else. This information is stored as a set of
     * key-to-value pairs, where keys may be also be keys to localized application resources (it
     * depends on the controller component).
     */
    public Map getProcessingAttributes()
    {
        return attributes;
    }


    /**
     * Returns a list (possibly null) of Throwable objects, which happened during processing and
     * describe processing errors.
     */
    public List getExceptions()
    {
        return exceptions;
    }


    /**
     * Adds a problem to the internal problem list.
     */
    public void addException(Throwable problem)
    {
        exceptions.add(problem);
    }


    /**
     * Adds a named attribute to the internal attributes list.
     */
    public void addAttribute(String key, Object value)
    {
        attributes.put(key, value);
    }
}
