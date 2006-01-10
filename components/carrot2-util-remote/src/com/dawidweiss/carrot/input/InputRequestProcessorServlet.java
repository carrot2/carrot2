
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

package com.dawidweiss.carrot.input;


import com.dawidweiss.carrot.util.CommonComponentInitializationServlet;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * An Input component type servlet, which wraps around an InputRequestProcessor object. Accepts
 * POST requests and parses, or delegates the request for further processing. The objective for
 * this class is to have one abstract HTTP servlet taking care of all POST request-related stuff
 * (parsing the request, for instance). Please subclass this class and override
 * <code>getInputRequestProcessor</code> method, which should return an object which will be used
 * for request handling. If you want to use this servlet directly (without subclassing), specify
 * as an initialization parameter <tt>inputRequestProcessor</tt>, the name of the class
 * subclassing <tt>InputRequestProcessor</tt>. A public parameterless constructor must be
 * available in this class.
 */
public class InputRequestProcessorServlet
    extends CommonComponentInitializationServlet
{
    /** The input request processor associated with this servlet. */
    InputRequestProcessor processor;

    /**
     * Initialize servlet context.
     *
     * @param servletConfig Servlet configuration passed from servlet container.
     */
    public void init(ServletConfig servletConfig)
        throws ServletException
    {
        super.init(servletConfig);

        String className = servletConfig.getInitParameter("inputRequestProcessor");

        if (className == null)
        {
            throw new ServletException(
                "You must define a class extending InputRequestProcessor in the input parameter 'inputRequestProcessor' of this servlet."
            );
        }

        try
        {
            processor = (InputRequestProcessor) Thread.currentThread().getContextClassLoader()
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
                "Request processor does not implement " + InputRequestProcessor.class.getName()
                + " interface."
            );
        }
    }


    /**
     * Override this method if you wish to return your own InputRequestProcessor. By default, this
     * method attempts to instantiate an input request processor object, which name is taken from
     * servlet initialization parameters.
     */
    public InputRequestProcessor getInputRequestProcessor()
    {
        return processor;
    }


    /**
     * Map the POST request to an XmlRpc or Carrot2 call.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Force UTF encoding on input.
        request.setCharacterEncoding("UTF-8");

        // process request. Let the container parse POST parameters (they're not streams anyway).
        String reqxml = request.getParameter("carrot-request");

        if (reqxml == null)
        {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Request must contain 'carrot-request' POST parameter."
            );

            return;
        }

        try
        {
            getInputRequestProcessor().processInputRequest(request, response);
        }
        catch (Throwable ex)
        {
            org.apache.log4j.Logger.getLogger(this.getClass()).error(
                "An internal component error occurred.", ex
            );
            try {
                response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal component error occurred: " + ex.toString()
                );
            } catch (IllegalStateException e) {
                // When response has been committed, an exception is thrown. Ignore.
                org.apache.log4j.Logger.getLogger(this.getClass()).warn(
                        "Could not send HTTP error (response comitted).");
            }

            return;
        }
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
        w.write(
            "<html><body>" + processor.getClass().getName()
            + " component serves requests using POST method.</body></html>"
        );
        w.close();
    }
}