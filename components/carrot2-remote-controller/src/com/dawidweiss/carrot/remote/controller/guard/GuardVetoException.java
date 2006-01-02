
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
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
