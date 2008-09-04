package org.carrot2.dcs;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.resource.ResourceUtilsFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A servlet that parses HTTP POST input in Carrot<sup>2</sup> XML format, clusters it and
 * returns clusters.
 */
@SuppressWarnings("serial")
public final class RestProcessorServlet extends HttpServlet
{
    final String DCS_CONFIG_ATTRIBUTE = "dcs.config";

    private DcsConfig config;

    private ProcessingComponentSuite componentSuite;

    private CachingController controller;

    @Override
    public void init() throws ServletException
    {
        // Check if config already injected by the console starter
        config = (DcsConfig) getServletContext().getAttribute(DCS_CONFIG_ATTRIBUTE);
        if (config != null)
        {
            config.logger.info("Console mode, skipping configuration in config.xml.");
        }
        else
        {
            // Run in servlet container, load config from config.xml
            try
            {
                config = DcsConfig.deserialize(ResourceUtilsFactory
                    .getDefaultResourceUtils().getFirst("config.xml"));
            }
            catch (Exception e)
            {
                throw new ServletException("Could not read config.xml", e);
            }
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
        final List<Class<? extends ProcessingComponent>> cachedComponentClasses = Lists
            .newArrayListWithExpectedSize(2);
        if (config.cacheDocuments)
        {
            cachedComponentClasses.add(DocumentSource.class);
        }
        if (config.cacheClusters)
        {
            cachedComponentClasses.add(ClusteringAlgorithm.class);
        }

        controller = new CachingController(cachedComponentClasses.toArray(new Class [2]));
        controller.init(Collections.<String, Object> emptyMap(), componentSuite);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final String command = getCommandName(request);
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
        else if ("statistics".equals(command))
        {
            try
            {
                response.setContentType("text/xml");
                controller.getStatistics().serialize(response.getWriter());
            }
            catch (Exception e)
            {
                sendInternalServerError("Could not serialize component information",
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
                final ProcessingResult result;
                try
                {
                    result = ProcessingResult.deserialize(uploadInputStream);
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
                documents = result.getDocuments();
            }
            else if (fileItem.isFormField())
            {
                parameters.put(fieldName, fileItem.getString());
            }

        }

        // Bind request parameters to the request model
        final DcsRequestModel requestModel = new DcsRequestModel();
        requestModel.algorithm = componentSuite.getAlgorithms().get(0).getId();
        final AttributeBinder.AttributeBinderActionBind attributeBinderActionBind = new AttributeBinder.AttributeBinderActionBind(
            Input.class, parameters, true,
            AttributeBinder.AttributeTransformerFromString.INSTANCE);
        try
        {
            AttributeBinder.bind(requestModel,
                new AttributeBinder.AttributeBinderAction []
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
                processingAttributes.put(AttributeNames.DOCUMENTS, documents);
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
            response.setContentType("text/xml");
            result.serialize(response.getWriter(), !requestModel.clustersOnly, true);
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
}
