

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.remote.controller.process;


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor;


/**
 * Classes implementing this interface are somehow capable of applying a process to a query,
 * producing some output.
 */
public interface ProcessDefinition
{
    /**
     * Returns the id string of this process. This ID must be unique and is used to find process
     * name in locales pool and for various indexing in collections.
     */
    public String getId();


    /**
     * Returns true if this process is scripted (i.e. the data flow and used components may change
     * from invocation to invocation according to external data - request parameters, time etc.
     */
    public boolean isScripted();


    /**
     * Returns true if this process uses the specified component. For scripted processes true is
     * returned even if the process can potentially use the component.
     *
     * @param component Check whether this component is potentially used in this process
     */
    public boolean usesComponent(ComponentDescriptor component);


    /**
     * Returns the default description for the process. The localized version should still be
     * included in the localized strings.
     */
    public String getDefaultDescription();
    
    
    /**
     * @return Should return <code>true</code> if this process is not to be explicitly shown
     *         in the user interface.
     */
    public boolean isHidden();
}
