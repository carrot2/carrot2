

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


import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * A collection of query guards.
 */
public class QueryGuardsSet
    implements QueryGuard
{
    private final ArrayList guards = new ArrayList();

    /**
     * Add a guard to the set. This method is not synchronized. Add guards before using the object.
     */
    public void addGuard(QueryGuard guard)
    {
        this.guards.add(guard);
    }


    /**
     * Return null to allow the query to be executed for the given component or any other string to
     * indicate an erraneous situation. The string will be looked up in the locales file and
     * displayed to the user.
     */
    public String allowInputComponent(
        Query q, ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    )
    {
        final int length = guards.size();
        String s;

        for (int i = 0; i < length; i++)
        {
            if (
                (s = ((QueryGuard) guards.get(i)).allowInputComponent(
                            q, component, session, request, context
                        )) != null
            )
            {
                return s;
            }
        }

        return null;
    }


    /**
     * Return null to allow the data stream to be passed to the given component or any other string
     * to indicate an erraneous situation. The string will be looked up in the locales file and
     * displayed to the user.
     */
    public String allowFilterComponent(
        ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    )
    {
        final int length = guards.size();
        String s;

        for (int i = 0; i < length; i++)
        {
            if (
                (s = ((QueryGuard) guards.get(i)).allowFilterComponent(
                            component, session, request, context
                        )) != null
            )
            {
                return s;
            }
        }

        return null;
    }


    public String toString()
    {
        return "GuardsSet [" + guards + "]";
    }
}
