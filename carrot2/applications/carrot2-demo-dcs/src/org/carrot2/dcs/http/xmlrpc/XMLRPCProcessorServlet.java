
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs.http.xmlrpc;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcServer;
import org.carrot2.dcs.http.AbstractProcessorServlet;
import org.carrot2.util.StringUtils;

/**
 * A clustering service accessible via XML-RPC.
 * 
 * @author Dawid Weiss
 */
public final class XMLRPCProcessorServlet extends AbstractProcessorServlet
{
    /**
     * XML-RPC handler for handling requests (<code>POST</code>s).
     */
    private XmlRpcServer xmlrpc;

    public void init() throws ServletException
    {
        super.init();

        if (isInitialized())
        {
            // Force UTF-8 encoding on the XML-RPC library.
            // This is a workaround for a clear bug in this library
            // (incoming encoding isn't properly handled).
            XmlRpc.setDefaultInputEncoding("UTF-8");
            XmlRpc.setEncoding("UTF-8");

            xmlrpc = new XmlRpcServer();

            final Carrot2XmlRpcHandler xmlRpcHandler = new Carrot2XmlRpcHandler(this.config);
            xmlrpc.addHandler("cluster", xmlRpcHandler);

            // Add test echo handler for testing encoding.
            xmlrpc.addHandler("test-encoding", new StringEncodingHandler());
        }
    }

    /**
     * <code>GET</code> requests cause our servlet to dump a debugging information along
     * with a bad-request response header.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
            "Use POST requests (XML-RPC handler).");
    }

    /**
     * We serve <code>POST</code> requests by passing them over to the XML-RPC handler.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try
        {
            // Pass the request to the handler for processing
            final byte [] result = xmlrpc.execute(request.getInputStream());

            response.setContentType("text/xml");
            response.setContentLength(result.length);
            final OutputStream out = response.getOutputStream();
            try
            {
                out.write(result);
            }
            finally
            {
                out.flush();
                out.close();
            }
        }
        catch (Exception e)
        {
            final String message = "An internal error occurred: " 
                + StringUtils.chainExceptionMessages(e);
            this.logger.warn(message, e);
            dcsLogger.warn(message);
        }
    }
}
