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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.carrot2.dcs.Config;
import org.carrot2.dcs.ConfigConstants;
import org.carrot2.dcs.ControllerContext;
import org.carrot2.dcs.ProcessingUtils;
import org.carrot2.dcs.http.InitializationServlet;
import org.carrot2.util.StringUtils;

/**
 * A servlet that parses HTTP POST input in Carrot<sup>2</sup> XML format, clusters it
 * and returns clusters.
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
     * Application configuration object.
     */
    private Config config;

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

        config = (Config) getServletContext().getAttribute(
            InitializationServlet.ATTR_APPCONFIG);
        if (config == null)
        {
            final String message = "Expected an configuration object in the servlet context.";
            logger.error(message);
            return;
        }

        this.dcsLogger = (Logger) config
            .getRequiredValue(ConfigConstants.ATTR_DCS_LOGGER);
        this.context = (ControllerContext) config
            .getRequiredValue(ConfigConstants.ATTR_CONTROLLER_CONTEXT);

        initialized = true;
    }

    /**
     * 
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if (!initialized)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Initialization failed. Check the logs.");
            return;
        }

        if (ServletFileUpload.isMultipartContent(request) == false)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Only requests with type multipart/form-data are supported.");
            return;
        }

        final ServletFileUpload upld = new ServletFileUpload(new MemoryFileItemFactory());
        try
        {
            final List items = upld.parseRequest(request);

            String processId = config
                .getRequiredString(ConfigConstants.ATTR_DEFAULT_PROCESSID);
            boolean clustersOnly = config
                .getRequiredBoolean(ConfigConstants.ATTR_CLUSTERS_ONLY);
            String outputProcessName = config
                .getRequiredString(ConfigConstants.ATTR_OUTPUT_FORMAT);

            for (Iterator i = items.iterator(); i.hasNext();)
            {
                final FileItem item = (FileItem) i.next();
                if ("c2stream".equals(item.getFieldName()))
                {
                    if (!this.context.getController().getProcessIds().contains(processId))
                    {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "No such algorithm: " + processId);
                        break;
                    }

                    // Run the query.
                    final InputStream inputStream = item.getInputStream();
                    final OutputStream outputStream = response.getOutputStream();
                    
                    if (outputProcessName.equals(ControllerContext.RESULTS_TO_JSON))
                    {
                        response.setContentType("text/json");
                    } else if (outputProcessName.equals(ControllerContext.RESULTS_TO_XML))
                    {
                        response.setContentType("text/xml");
                    }

                    try
                    {
                        ProcessingUtils.cluster(context.getController(), dcsLogger,
                            inputStream, outputStream, processId, outputProcessName,
                            clustersOnly);
                    }
                    catch (Throwable e)
                    {
                        final String message = "Clustering failed: "
                            + StringUtils.chainExceptionMessages(e);
                        dcsLogger.error(message);
                        logger.error(message, e);
                        if (!response.isCommitted())
                        {
                            response.sendError(
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                        }

                        if (outputStream != null) outputStream.close();
                        if (inputStream != null) inputStream.close();
                        break;
                    }

                    return;
                }
                else if (ConfigConstants.ATTR_DEFAULT_PROCESSID.equals(item
                    .getFieldName()))
                {
                    final String newValue = item.getString();
                    if (!"".equals(newValue.trim()))
                    {
                        processId = newValue;
                    }
                }
                else if (ConfigConstants.ATTR_CLUSTERS_ONLY.equals(item.getFieldName()))
                {
                    clustersOnly = Boolean.valueOf(item.getString()).booleanValue();
                    logger.debug("Clusters only switch (request): " + clustersOnly);
                }
                else if (ConfigConstants.ATTR_OUTPUT_FORMAT.equals(item.getFieldName()))
                {
                    final String outputFormatName = item.getString();
                    if ("".equals(outputFormatName))
                    {
                        // Ignore.
                    }
                    else if ("xml".equals(outputFormatName))
                    {
                        outputProcessName = ControllerContext.RESULTS_TO_XML;
                        logger.debug("Request output format set to " + outputFormatName);
                    }
                    else if ("json".equals(outputFormatName))
                    {
                        outputProcessName = ControllerContext.RESULTS_TO_JSON;
                        logger.debug("Request output format set to " + outputFormatName);
                    }
                    else
                    {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Request format invalid: " + outputFormatName);
                        return;
                    }
                }
                else
                {
                    // skip this part.
                }
            }
            logger.info("Missing 'c2stream' request parameter.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        catch (FileUploadException e)
        {
            logger.warn("File upload request failed: "
                + StringUtils.chainExceptionMessages(e));
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        catch (Throwable t)
        {
            final String message = "Internal server error: "
                + StringUtils.chainExceptionMessages(t);
            logger.warn(message, t);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
}
