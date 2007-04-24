
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs.http.rest;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.carrot2.dcs.ControllerContext;
import org.carrot2.dcs.ProcessingUtils;
import org.carrot2.dcs.http.ServletContextConstants;
import org.carrot2.util.StringUtils;

/**
 * A servlet that parses HTTP POST input in Carrot<sup>2</sup> XML format, clusters it and returns clusters.
 */
public final class XMLProcessorServlet extends HttpServlet
{
    /**
     * Local logger.
     */
    private Logger logger = Logger.getLogger(XMLProcessorServlet.class);

    /**
     * DCS logger.
     */
    private Logger dcsLogger;

    /**
     * Controller context for processing requests.
     */
    private ControllerContext context;

    /**
     * Default process identifier.
     */
    private String defaultProcessId;

    /**
     * Indicates if the servlet was initialized properly.
     */
    private boolean initialized;

    /**
     * 
     */
    public void init() throws ServletException
    {
        super.init();

        this.dcsLogger = (Logger) getServletContext().getAttribute(ServletContextConstants.ATTR_DCS_LOGGER);

        this.context = (ControllerContext) getServletContext().getAttribute(
            ServletContextConstants.ATTR_CONTROLLER_CONTEXT);
        if (context == null)
        {
            final String message = "Expected an instance of a controller context in the servlet context.";
            logger.error(message);
            dcsLogger.error(message);
            return;
        }

        this.defaultProcessId = (String) getServletContext().getAttribute(
            ServletContextConstants.ATTR_DEFAULT_PROCESSID);
        if (this.defaultProcessId == null)
        {
            final String message = "Expected default algorithm name in the servlet context.";
            logger.error(message);
            dcsLogger.error(message);
            return;
        }

        initialized = true;
    }

    /**
     * 
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException
    {
        if (!initialized)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Initialization failed. Check the logs.");
            return;
        }

        if (ServletFileUpload.isMultipartContent(request) == false)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only multipart requests supported.");
            return;
        }

        final ServletFileUpload upld = new ServletFileUpload(new MemoryFileItemFactory());
        try
        {
            final List items = upld.parseRequest(request);

            String processId = defaultProcessId;
            for (Iterator i = items.iterator(); i.hasNext();)
            {
                final FileItem item = (FileItem) i.next();
                if ("c2stream".equals(item.getFieldName()))
                {
                    if (!this.context.getController().getProcessIds().contains(processId))
                    {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No such algorithm: " + processId);
                        break;
                    }

                    // Run the query.
                    final InputStream inputStream = item.getInputStream();
                    final OutputStream outputStream = response.getOutputStream();
                    response.setContentType("text/xml");

                    try
                    {
                        ProcessingUtils.cluster(processId, context.getController(), dcsLogger, inputStream,
                            outputStream);
                    }
                    catch (Throwable e)
                    {
                        final String message = "Clustering failed: " + StringUtils.chainExceptionMessages(e);
                        logger.error(message, e);
                        if (!response.isCommitted())
                        {
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                        }

                        if (outputStream != null) outputStream.close();
                        if (inputStream != null) inputStream.close();
                        break;
                    }

                    return;
                }
                else if ("algorithm".equals(item.getFieldName()))
                {
                    final String newValue = item.getString();
                    if (!"".equals(newValue.trim()))
                    {
                        processId = newValue;
                    }
                }
                else
                {
                    // skip this part.
                }
            }
            logger.info("Missing 'c2stream' request parameter.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (FileUploadException e)
        {
            logger.warn("File upload request failed: " + StringUtils.chainExceptionMessages(e));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        catch (Throwable t)
        {
            final String message = "Internal server error: " + StringUtils.chainExceptionMessages(t);
            logger.warn(message, t);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
}
