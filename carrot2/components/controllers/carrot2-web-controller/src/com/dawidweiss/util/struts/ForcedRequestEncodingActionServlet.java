

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.util.struts;


import org.apache.struts.action.ActionServlet;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * This is a subclass of ActionServlet, which overrides input request encoding to the value
 * specified in servlet parameter 'forcedRequestEncoding'.
 */
public class ForcedRequestEncodingActionServlet
    extends ActionServlet
{
    /**
     * This is the forced encoding to which every request should be set. If this is null, no forced
     * encoding is set on requests.
     */
    private String encoding;

    /**
     * Initialize servlet. If no forced encoding has been specified, just forget it and do nothing.
     */
    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);

        if (config.getInitParameter("forcedRequestEncoding") != null)
        {
            try
            {
                encoding = config.getInitParameter("forcedRequestEncoding");
                new String(
                    "This is a test of the encoding specified.".getBytes(encoding), encoding
                );
                log(
                    "Forced encoding set to " + encoding + " on servlet " + config.getServletName()
                );
            }
            catch (java.io.UnsupportedEncodingException e)
            {
                encoding = null;
                log(
                    "Unsupported encoding " + encoding
                    + " specified for request conversion on servlet " + config.getServletName()
                );
                throw new ServletException("Unsupported encoding: " + encoding);
            }
        }
        else
        {
            this.log(
                "No forced request encoding conversion has been specified for servlet: "
                + config.getServletName()
            );
        }
    }


    /**
     * Override encoding of parameters on GET requests and delegate processing to superclass.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        if (encoding != null)
        {
            request.setCharacterEncoding(encoding);
        }

        super.doGet(request, response);
    }


    /**
     * Override encoding of parameters on POST requests and delegate processing to superclass.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        if (encoding != null)
        {
            request.setCharacterEncoding(encoding);
        }

        super.doPost(request, response);
    }
}
