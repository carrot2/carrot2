
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
package com.dawidweiss.carrot.util;


import javax.servlet.ServletConfig;


/**
 * This is an abstract request processor class, used in template methods.
 */
public abstract class AbstractRequestProcessor
{
    private ServletConfig servletConfig;

    /**
     * Sets the servlet configuration. This method is invoked by template class instantiating the
     * request processor.
     */
    public void setServletConfig(ServletConfig servletConfig)
    {
        this.servletConfig = servletConfig;
    }


    /**
     * Returns the configuration of a servlet to which this request processor is linked.
     */
    public final ServletConfig getServletConfig()
    {
        return servletConfig;
    }
}
