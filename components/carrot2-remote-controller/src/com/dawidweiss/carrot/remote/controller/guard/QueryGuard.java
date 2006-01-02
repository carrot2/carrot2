
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


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Instances of this interface are invoked before each query. The guard may veto query submission
 * and return an information about the cause of the problem (for example, limited availability of
 * a certain component etc).
 */
public interface QueryGuard
{
    /**
     * Return null to allow the query to be executed for the given component or any other string to
     * indicate an erraneous situation. The string will be looked up in the locales file and
     * displayed to the user.
     */
    public String allowInputComponent(
        Query q, ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    );


    /**
     * Return null to allow the data stream to be passed to the given component or any other string
     * to indicate an erraneous situation. The string will be looked up in the locales file and
     * displayed to the user.
     */
    public String allowFilterComponent(
        ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    );
}
