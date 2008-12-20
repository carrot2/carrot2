
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

package org.carrot2.dcs;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;
import org.carrot2.core.*;
import org.carrot2.dcs.DcsRequestModel.OutputFormat;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.ResourceUtilsFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A servlet that parses HTTP POST input in Carrot<sup>2</sup> XML format, clusters it and
 * returns clusters.
 */
public final class RestProcessorServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private transient DcsConfig config;

    private transient ProcessingComponentSuite componentSuite;

    private transient CachingController controller;

    private transient boolean loggerInitialized;

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException
    {
        // Run in servlet container, load config from config.xml
        try
        {
            config = DcsConfig.deserialize(ResourceUtilsFactory.getDefaultResourceUtils()
                .getFirst("config.xml"));
        }
        catch (Exception e)
        {
            throw new ServletException("Could not read config.xml", e);
        }

        // Load component suite
        try
        {
            componentSuite = ProcessingComponentSuite.deserialize(ResourceUtilsFactory
                .getDefaultResourceUtils().getFirst("carrot2-default/suite-dcs.xml"));
        }
        catch (Exception e)
        {
            throw new ServletException("Could intialize component suite", e);
        }

        // Initialize controller
        final List<Class<? extends IProcessingComponent>> cachedComponentClasses = Lists
            .newArrayListWithExpectedSize(2);
        if (config.cacheDocuments)
        {
            cachedComponentClasses.add(IDocumentSource.class);
        }
        if (config.cacheClusters)
        {
            cachedComponentClasses.add(IClusteringAlgorithm.class);
        }

        controller = new CachingController(cachedComponentClasses.toArray(new Class [2]));
        controller.init(Collections.<String, Object> emptyMap(), componentSuite);
    }

    @Override
    public void destroy()
    {
        if (this.controller != null)
        {
            this.controller.dispose();
            this.controller = null;
        }

        super.destroy();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse arg1)
        throws ServletException, IOException
    {
        synchronized (this)
        {
            if (!loggerInitialized)
            {
                Logger.getRootLogger().addAppender(getLogAppender(request));
                loggerInitialized = true;
            }
        }
        super.service(request, arg1);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final String command = getCommandName(request);
        response.setCharacterEncoding("utf-8");
        if ("components".equals(command))
        {
            try
            {
                response.setContentType("text/xml");
                componentSuite.serialize(response.getWriter());
            }
            catch (Exception e)
            {
                sendInternalServerError("Could not serialize component information",
                    response, e);
                return;
            }
        }
        else if ("input-example".equals(command))
        {
            try
            {
                response.setContentType("text/xml");
                EXAMPLE_INPUT.serialize(response.getWriter(), true, false);
            }
            catch (Exception e)
            {
                sendInternalServerError("Could not serialize input format example",
                    response, e);
                return;
            }
        }
        else if ("output-example-xml".equals(command))
        {
            try
            {
                response.setContentType("text/xml");
                EXAMPLE_OUTPUT.serialize(response.getWriter());
            }
            catch (Exception e)
            {
                sendInternalServerError("Could not serialize output format XML example",
                    response, e);
                return;
            }
        }
        else if ("output-example-json".equals(command))
        {
            try
            {
                response.setContentType("text/json");
                EXAMPLE_OUTPUT
                    .serializeJson(response.getWriter(), null, true, true, true);
            }
            catch (Exception e)
            {
                sendInternalServerError("Could not serialize output format JSON example",
                    response, e);
                return;
            }
        }
        else if ("status".equals(command))
        {
            try
            {
                response.setContentType("text/xml");
                controller.getStatistics().serialize(response.getWriter());
            }
            catch (Exception e)
            {
                sendInternalServerError("Could not serialize status information",
                    response, e);
                return;
            }
        }
        else
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown command: '"
                + command + "'");
        }
    }

    /**
     * Handle REST requests (HTTP POST with multipart/form-data content).
     */
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if (ServletFileUpload.isMultipartContent(request) == false)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Only requests with type multipart/form-data are supported.");
            return;
        }

        // Documents will remain null if they were not provided. In that
        // case most probably a query to an external document source was requested
        List<Document> documents = null;
        ProcessingResult input = null;

        // Parse uploaded file
        final ServletFileUpload upload = new ServletFileUpload(
            new MemoryFileItemFactory());
        final List<FileItem> items;
        try
        {
            items = upload.parseRequest(request);
        }
        catch (FileUploadException e1)
        {
            sendBadRequest("Could not parse multipart/form-data", response, e1);
            return;
        }

        // Extract uploaded data and other parameters
        final Map<String, Object> parameters = Maps.newHashMap();
        for (FileItem fileItem : items)
        {
            final String fieldName = fileItem.getFieldName();
            if ("dcs.c2stream".equals(fieldName))
            {
                final InputStream uploadInputStream;
                if (fileItem.isFormField())
                {
                    uploadInputStream = new ByteArrayInputStream(fileItem.get());
                }
                else
                {
                    uploadInputStream = fileItem.getInputStream();
                }

                // Deserialize documents from the stream
                try
                {
                    input = ProcessingResult.deserialize(new InputStreamReader(
                        uploadInputStream, "utf-8"));
                }
                catch (Exception e)
                {
                    sendBadRequest("Could not parse Carrot2 XML stream", response, e);
                    return;
                }
                finally
                {
                    CloseableUtils.close(uploadInputStream);
                }
                documents = input.getDocuments();
            }
            else if (fileItem.isFormField())
            {
                parameters.put(fieldName, fileItem.getString());
            }

        }

        // Remove useless parameters, we don't want them to get to the attributes map
        parameters.remove("input-type");
        parameters.remove("submit");

        // Bind request parameters to the request model
        final DcsRequestModel requestModel = new DcsRequestModel();

        final AttributeBinder.AttributeBinderActionBind attributeBinderActionBind = new AttributeBinder.AttributeBinderActionBind(
            Input.class, parameters, true,
            AttributeBinder.AttributeTransformerFromString.INSTANCE);
        try
        {
            AttributeBinder.bind(requestModel,
                new AttributeBinder.IAttributeBinderAction []
                {
                    attributeBinderActionBind
                }, Input.class);
        }
        catch (Exception bindingException)
        {
            sendInternalServerError("Could not bind request parameters", response,
                bindingException);
        }

        // Pass the remaining request attributes directly to the controller
        final Map<String, Object> processingAttributes = attributeBinderActionBind.remainingValues;

        // Also add the attributes (e.g. query) from the input processing result
        if (input != null)
        {
            processingAttributes.putAll(input.getAttributes());
        }
        
        if (StringUtils.isEmpty(requestModel.algorithm))
        {
            requestModel.algorithm = componentSuite.getAlgorithms().get(0).getId();            
        }

        // We need either sourceId or direct document feed
        if (requestModel.source == null && documents == null)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Either dcs.source or dcs.c2stream must be provided");
            return;
        }

        // Perform processing
        ProcessingResult result = null;
        try
        {
            if (requestModel.source != null)
            {
                config.logger.info("Processing results from " + requestModel.source
                    + " with " + requestModel.algorithm);
                result = controller.process(processingAttributes, requestModel.source,
                    requestModel.algorithm);
            }
            else
            {
                config.logger.info("Processing direct results feed with "
                    + requestModel.algorithm);
                result = controller.process(processingAttributes, requestModel.algorithm);
            }
        }
        catch (ProcessingException e)
        {
            sendInternalServerError("Could not perform processing", response, e);
            return;
        }

        // Serialize the result
        try
        {
            response.setCharacterEncoding("utf-8");
            if (OutputFormat.XML.equals(requestModel.outputFormat))
            {
                response.setContentType(OutputFormat.XML.contentType);
                result.serialize(response.getWriter(), !requestModel.clustersOnly, true);
            }
            else if (OutputFormat.JSON.equals(requestModel.outputFormat))
            {
                response.setContentType(OutputFormat.JSON.contentType);
                result.serializeJson(response.getWriter(), requestModel.jsonCallback,
                    !requestModel.clustersOnly, true);
            }
            else
            {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Unknown output format: '" + requestModel.outputFormat + "'");
                return;
            }
        }
        catch (Exception e)
        {
            sendInternalServerError("Could not serialize results", response, e);
        }
    }

    /**
     * A very simplistic calculation of command name from the request URI.
     */
    private String getCommandName(HttpServletRequest request)
    {
        final String uri = request.getRequestURI();
        final int slashIndex = uri.lastIndexOf('/');

        String command;
        if (slashIndex >= 0)
        {
            command = uri.substring(slashIndex + 1);
        }
        else
        {
            command = uri;
        }
        return command;
    }

    private void sendInternalServerError(String message, HttpServletResponse response,
        Throwable e) throws IOException
    {
        final String finalMessage = message + ": " + e.getMessage();
        config.logger.warn(finalMessage);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, finalMessage);
    }

    private void sendBadRequest(String message, HttpServletResponse response, Throwable e)
        throws IOException
    {
        final String finalMessage = message + ": " + e.getMessage();
        config.logger.warn(finalMessage);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, finalMessage);
    }

    private FileAppender getLogAppender(HttpServletRequest request) throws IOException
    {
        String contextPath = request.getContextPath();
        if (StringUtils.isBlank(contextPath))
        {
            contextPath = "root";
        }
        contextPath = contextPath.replaceAll("[^a-zA-Z0-9\\-]", "");
        final String catalinaHome = System.getProperty("catalina.home");
        final String logPrefix = (catalinaHome != null ? catalinaHome + "/logs" : "logs");

        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601} [%-5p] [%c] %m%n"), logPrefix + "/c2-dcs-" + contextPath
            + "-full.log", true);
        appender.setEncoding("UTF-8");
        return appender;
    }

    /**
     * {@link ProcessingResult} served as input/output example.
     */
    private final static ProcessingResult EXAMPLE_INPUT;
    private final static ProcessingResult EXAMPLE_OUTPUT;
    static
    {
        InputStream streamInput = null;
        InputStream streamOutput = null;
        try
        {
            streamInput = new ClassResource(RestProcessorServlet.class,
                "example-input.xml").open();
            EXAMPLE_INPUT = ProcessingResult.deserialize(new InputStreamReader(
                streamInput, "utf-8"));
            streamOutput = new ClassResource(RestProcessorServlet.class,
                "example-output.xml").open();
            EXAMPLE_OUTPUT = ProcessingResult.deserialize(new InputStreamReader(
                streamOutput, "utf-8"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not load example data", e);
        }
        finally
        {
            CloseableUtils.close(streamInput, streamOutput);
        }
    }
}
