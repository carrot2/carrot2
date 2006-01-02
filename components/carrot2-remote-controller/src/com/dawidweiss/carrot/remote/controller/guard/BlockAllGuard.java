
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
import org.apache.log4j.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Limits the number of queries to a given component per hour.
 */
public class BlockAllGuard
    implements QueryGuard, com.dawidweiss.carrot.remote.controller.util.PostConfigureCheck
{
    private static final Logger log = Logger.getLogger(BlockAllGuard.class);
    private String counterName;
    private String componentId;
    private String componentPrefix;

    /**
     * Sets the ID prefix of a family of components to count
     */
    public void setComponentPrefix(String prefix)
    {
        if (this.componentId != null)
        {
            throw new IllegalArgumentException("Set either prefix or component id.");
        }

        if (this.componentPrefix != null)
        {
            throw new IllegalArgumentException("Set prefix only once");
        }

        this.componentPrefix = prefix;
    }


    /**
     * Sets the ID of a component to count
     */
    public void setComponentId(String componentId)
    {
        if (this.componentId != null)
        {
            throw new IllegalArgumentException("Set componentId only once");
        }

        if (this.componentPrefix != null)
        {
            throw new IllegalArgumentException("Set either prefix or component id");
        }

        this.componentId = componentId;
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
        return allow(component, session, request, context);
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
        return allow(component, session, request, context);
    }


    /**
     * Common for input/filter components.
     */
    private final String allow(
        ComponentDescriptor component, HttpSession session, HttpServletRequest request,
        ServletContext context
    )
    {
        if (
            ((componentPrefix != null) && component.getId().startsWith(componentPrefix))
                || ((componentId != null) && component.getId().equals(this.componentId))
        )
        {
            return "component-blocked";
        }

        return null;
    }


    /**
     * Make sure everything has been configured as needed.
     */
    public String assertConfigured()
    {
        if ((componentId == null) && (componentPrefix == null))
        {
            return "componentId or componentPrefix property is required";
        }

        return null;
    }
}
