
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.remote.controller.process.scripted;


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.ComponentDescriptor;


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
