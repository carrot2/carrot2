

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


package com.dawidweiss.carrot.remote.controller.process.scripted;


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor;


/**
 * Thrown when processing of a request has been interrupted because of a failure of one of the
 * components.
 */
public class ComponentFailureException
    extends Exception
{
    private ComponentDescriptor component;

    public ComponentFailureException(ComponentDescriptor component, String failureMessage)
    {
        super(failureMessage);
        this.component = component;
    }

    public String toString()
    {
        return "Component failure [" + component.getId() + "]: " + getMessage();
    }
}
