

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


/**
 * Thrown when the a component referenced in a processing chain is not available.
 */
public class ComponentNotAvailableException
    extends Exception
{
    public ComponentNotAvailableException(String componentName)
    {
        super(componentName);
    }
}
