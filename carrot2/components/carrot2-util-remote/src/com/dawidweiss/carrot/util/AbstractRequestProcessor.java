/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
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
