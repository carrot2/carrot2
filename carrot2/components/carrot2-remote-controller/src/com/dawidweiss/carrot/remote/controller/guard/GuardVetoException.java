

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


package com.dawidweiss.carrot.remote.controller.guard;


import com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*;


public class GuardVetoException
    extends ComponentFailureException
{
    public GuardVetoException(ComponentDescriptor component, String failureMessage)
    {
        super(component, failureMessage);
    }
}
