
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

package com.dawidweiss.carrot.filter.stc.servlet;


import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Performs the first step of the Suffix Tree Clustering algorithm - discovery of base clusters.
 */
public class STCPhase1Servlet
    extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());

    /**
     * Sets the servlet configuration. This method is invoked by template class instantiating the
     * request processor.
     */
    public void setServletConfig(ServletConfig servletConfig)
    {
        super.setServletConfig(servletConfig);
    }


    /**
     * Processes a Carrot2 request.
     */
    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response,
        Map paramsBeforeData
    )
        throws Exception
    {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not yet implemented.");
    }
}
