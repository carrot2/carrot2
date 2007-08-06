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
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.carrot2.dcs.*;
import org.carrot2.dcs.http.AbstractProcessorServlet;
import org.carrot2.util.StringUtils;

/**
 * A servlet that parses HTTP POST input in Carrot<sup>2</sup> XML format, clusters it
 * and returns clusters.
 */
public final class RESTProcessorServlet extends AbstractProcessorServlet
{
    /**
     * Handle REST requests (HTTP POST with multipart/form-data content).
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if (!isInitialized())
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

            final Map processingDefaults = config.getProcessingDefaults();
            final HashMap overrides = new HashMap(processingDefaults);
            for (Iterator i = items.iterator(); i.hasNext();)
            {
                final FileItem item = (FileItem) i.next();
                if ("c2stream".equals(item.getFieldName()))
                {
                    final String processId = (String) overrides.get(ProcessingOptionNames.ATTR_PROCESSID);
                    if (!this.context.getController().getProcessIds().contains(processId))
                    {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "No such process: " + processId + " (available processes: "
                            + StringUtils.toString(this.context.getProcessIds(), ", "));
                        break;
                    }

                    // Run the query.
                    final InputStream inputStream = item.getInputStream();
                    final OutputStream outputStream = response.getOutputStream();

                    final String outputFormat = (String) overrides.get(ProcessingOptionNames.ATTR_OUTPUT_FORMAT);
                    response.setContentType(ControllerContext.getContentTypeFor(outputFormat));

                    try
                    {
                        ProcessingUtils.cluster(context.getController(), dcsLogger,
                            inputStream, outputStream, overrides);
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
                else
                {
                    final String fieldName = item.getFieldName();
                    final String fieldValue = item.getString();
                    if (fieldValue != null && !"".equals(fieldValue)) 
                    {
                        overrides.put(fieldName, fieldValue);
                    }
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
