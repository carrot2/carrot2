/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.filter;


import com.dawidweiss.carrot.util.CommonComponentInitializationServlet;
import com.dawidweiss.carrot.util.http.PostRequestElement;
import com.dawidweiss.carrot.util.http.PostRequestParametersIterator;
import org.apache.log4j.Logger;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A Filter component type servlet, which wraps around a FilterRequestProcessor object. Accepts
 * POST requests and parses, or delegates the request for further processing. The objective for
 * this class is to have one abstract HTTP servlet taking care of all POST request-related stuff
 * (parsing the request, for instance). Please subclass this class and override
 * <code>getFilterRequestProcessor</code> method, which should return an object which will be used
 * for request handling. If you want to use this servlet directly (without subclassing), specify
 * as an initialization parameter <tt>filterRequestProcessor</tt>, the name of the class
 * subclassing <tt>FilterRequestProcessor</tt>. A public parameterless constructor must be
 * available in this class.
 */
public class FilterRequestProcessorServlet
    extends CommonComponentInitializationServlet
{
    /** Logger for the template servlet. */
    protected final Logger log = Logger.getLogger(this.getClass());

    /** The input request processor associated with this servlet. */
    FilterRequestProcessor processor;

    /**
     * Initialize servlet context.
     *
     * @param servletConfig Servlet configuration passed from servlet container.
     */
    public void init(ServletConfig servletConfig)
        throws ServletException
    {
        super.init(servletConfig);

        String className = servletConfig.getInitParameter("filterRequestProcessor");

        if (className == null)
        {
            throw new ServletException(
                "You must define a class extending FilterRequestProcessor in the input parameter 'filterRequestProcessor' of this servlet."
            );
        }

        try
        {
            processor = (FilterRequestProcessor) Thread.currentThread().getContextClassLoader()
                                                       .loadClass(className).newInstance();
            processor.setServletConfig(servletConfig);
        }
        catch (ClassNotFoundException ex)
        {
            throw new ServletException("Request processor class not found: " + ex.getMessage());
        }
        catch (IllegalAccessException ex)
        {
            throw new ServletException("Cannot access request processor class: " + ex.getMessage());
        }
        catch (InstantiationException ex)
        {
            throw new ServletException(
                "Cannot instantiate request processor class: " + ex.getMessage()
            );
        }
        catch (ClassCastException ex)
        {
            throw new ServletException(
                "Request processor does not implement " + FilterRequestProcessor.class.getName()
                + " interface."
            );
        }
    }


    /**
     * Override this method if you wish to return your own InputRequestProcessor. By default, this
     * method attempts to instantiate an input request processor object, which name is taken from
     * servlet initialization parameters.
     */
    public FilterRequestProcessor getFilterRequestProcessor()
    {
        return processor;
    }


    /**
     * Map the POST request to an XmlRpc or Carrot2 call.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        Map params = new HashMap();

        // parse POST stream until 'carrot-xchange-data' parameter is encountered.
        // all parameters before that one are preserved in a hashmap.
        ServletInputStream postedData = req.getInputStream();

        // Locale responseLocale = req.getLocale();
        // detect POST message type? We currently don't support multipart POSTs. Only
        // application/x-www-form-urlencoded forms are allowed.
        // Iterate over the parameters passed in POST request, look for carrot XML data.
        Iterator i = new PostRequestParametersIterator(
                new BufferedInputStream(postedData, /* read buffer */
                    8000
                ), "iso8859-1"
            );

        while (i.hasNext())
        {
            PostRequestElement p = (PostRequestElement) i.next();

            if ("carrot-xchange-data".equals(p.getParameterName()))
            {
                InputStream carrotData = p.getParameterValueAsInputStream();

                try
                {
                    getFilterRequestProcessor().processFilterRequest(carrotData, req, res, params);
                }
                catch (Throwable e)
                {
                    log.error(
                        "An internal component error occurred in processor "
                        + processor.getClass().getName() + ": " + e.toString(), e
                    );
                    res.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An internal component error occurred in processor "
                        + processor.getClass().getName() + ": " + e.toString()
                    );
                }

                return;
            }
            else
            {
                List val;

                if ((val = (List) params.get(p.getParameterName())) == null)
                {
                    val = new LinkedList();
                    params.put(p.getParameterName(), val);
                }

                val.add(p.getParameterValueAsString());
            }
        }

        // No carrot-exchange-data
        res.sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            "Request must contain 'carrot-xchange-data' POST parameter."
        );
        log.error("Request must contain 'carrot-xchange-data' POST parameter.");
    }


    /**
     * Return a plain HTML with the name of processor used for processing POSTs to this URL.
     * Override as needed.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        OutputStream output = res.getOutputStream();
        Writer w = new OutputStreamWriter(output, "UTF-8");
        w.write("<html><body>" + processor.getClass().getName() + " works here.</body></html>");
        w.close();
    }


    // ------------------------------------------------------- protected section

    /**
     * Returns an error to the output servlet stream.
     */
    protected void returnError(String errorCode, HttpServletResponse res)
    {
        try
        {
            res.getOutputStream().println("@@ERROR: " + errorCode);
        }
        catch (IOException e)
        {
            // can't do much about it.
        }
    }
}
