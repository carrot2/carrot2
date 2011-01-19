
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.IProcessingComponent;
import org.carrot2.core.ProcessingComponentSuite;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.ProcessingResult;
import org.carrot2.dcs.DcsRequestModel.OutputFormat;
import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.IResourceLocator;
import org.carrot2.util.resource.PrefixDecoratorLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.resource.ServletContextLocator;
import org.carrot2.util.xslt.NopURIResolver;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A servlet that parses HTTP POST input in Carrot<sup>2</sup> XML format, clusters it and
 * returns clusters.
 */
public final class RestProcessorServlet extends HttpServlet
{
    /** System property to disable log file appender. */
    final static String DISABLE_LOGFILE_APPENDER = "disable.logfile";

    /** System property to enable class path search for resources in tests. */
    final static String ENABLE_CLASSPATH_LOCATOR = "enable.classpath.locator";

    /** Response constants */
    private final static String UTF8 = "UTF-8";
    private final static String MIME_XML_UTF8 = "text/xml; charset=" + UTF8;
    private final static String MIME_JSON_UTF8 = "text/json; charset=" + UTF8;

    private static final long serialVersionUID = 1L;

    private transient DcsConfig config;

    private transient ProcessingComponentSuite componentSuite;

    private transient Controller controller;

    private transient boolean loggerInitialized;

    private String defaultAlgorithmId;

    private transient Templates xsltTemplates;

    /**
     * Disable log file appender configured in
     * {@link #getLogAppender(HttpServletRequest)}. 
     * The appender is enabled by default, but disabled
     * for tests.
     */
    private boolean disableLogFileAppender = Boolean.getBoolean(DISABLE_LOGFILE_APPENDER);

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException
    {
        // Run in servlet container, load config from config.xml.
        ResourceLookup webInfLookup = new ResourceLookup(
            new PrefixDecoratorLocator(
                new ServletContextLocator(getServletContext()), "/WEB-INF/")
        );

        try
        {
            config = DcsConfig.deserialize(webInfLookup.getFirst("dcs-config.xml"));
        }
        catch (Exception e)
        {
            throw new ServletException("Could not read 'config.xml' resource.", e);
        }

        // Initialize XSLT
        initXslt(config, webInfLookup);

        // Load component suite. Use classpath too (for JUnit tests).
        try
        {
            List<IResourceLocator> resourceLocators = Lists.newArrayList();
            resourceLocators.add(new PrefixDecoratorLocator(
                new ServletContextLocator(getServletContext()), "/WEB-INF/suites/"));

            if (Boolean.getBoolean(ENABLE_CLASSPATH_LOCATOR))
                resourceLocators.add(Location.CONTEXT_CLASS_LOADER.locator);

            ResourceLookup suitesLookup = new ResourceLookup(resourceLocators);

            IResource suiteResource = suitesLookup.getFirst(config.componentSuiteResource);
            if (suiteResource == null)
            {
                throw new Exception("Suite file not found in servlet context's /WEB-INF/suites: " 
                    + config.componentSuiteResource);
            }
            componentSuite = ProcessingComponentSuite.deserialize(suiteResource, suitesLookup);
        }
        catch (Exception e)
        {
            throw new ServletException("Could initialize component suite.", e);
        }

        // Initialize defaults.
        if (componentSuite.getAlgorithms().size() == 0)
        {
            throw new ServletException("Component suite has no algorithms.");
        }
        defaultAlgorithmId = componentSuite.getAlgorithms().get(0).getId();

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

        controller = ControllerFactory.createCachingPooling(cachedComponentClasses
            .toArray(new Class [cachedComponentClasses.size()]));

        List<IResourceLocator> locators = Lists.newArrayList();
        locators.add(new PrefixDecoratorLocator(
            new ServletContextLocator(getServletContext()), "/WEB-INF/resources/"));

        if (Boolean.getBoolean(ENABLE_CLASSPATH_LOCATOR))
            locators.add(Location.CONTEXT_CLASS_LOADER.locator);

        controller.init(
            ImmutableMap.<String, Object> of(
                AttributeUtils.getKey(DefaultLexicalDataFactory.class, "resourceLookup"),
                new ResourceLookup(locators)),
            componentSuite.getComponentConfigurations());
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

    /**
     * 
     */
    private void initXslt(DcsConfig config, ResourceLookup resourceLookup)
    {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        tFactory.setURIResolver(new NopURIResolver());

        InputStream xsltStream = null;

        if (StringUtils.isNotBlank(config.xslt))
        {
            IResource resource = resourceLookup.getFirst(config.xslt);
            if (resource == null)
            {
                config.logger.warn("XSLT stylesheet " + config.xslt
                    + " not found. No XSLT transformation will be applied.");
                return;
            }

            try
            {
                xsltStream = resource.open();
                xsltTemplates = tFactory.newTemplates(new StreamSource(xsltStream));
                config.logger.info("XSL stylesheet loaded successfully from: "
                    + config.xslt);
            }
            catch (IOException e)
            {
                config.logger.warn(
                    "Could not load stylesheet, no XSLT transform will be applied.", e);
            }
            catch (TransformerConfigurationException e)
            {
                config.logger.warn(
                    "Could not load stylesheet, no XSLT transform will be applied", e);
            }
            finally
            {
                CloseableUtils.close(xsltStream);
            }
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse arg1)
        throws ServletException, IOException
    {
        synchronized (this)
        {
            if (!loggerInitialized)
            {
                if (!disableLogFileAppender)
                {
                    Logger.getRootLogger().addAppender(getLogAppender(request));
                }
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
        if ("components".equals(command))
        {
            try
            {
                response.setContentType(MIME_XML_UTF8);
                componentSuite.serialize(response.getOutputStream());
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
                response.setContentType(MIME_XML_UTF8);
                EXAMPLE_INPUT.serialize(response.getOutputStream(), true, false);
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
                transformAndSerializeOutputXml(response, EXAMPLE_OUTPUT, true, true);
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
                response.setContentType(MIME_JSON_UTF8);
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
                response.setContentType(MIME_XML_UTF8);
                controller.getStatistics().serialize(response.getOutputStream());
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
                    input = ProcessingResult.deserialize(uploadInputStream);
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
            parameters, true, AttributeBinder.AttributeTransformerFromString.INSTANCE);
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
            requestModel.algorithm = defaultAlgorithmId;
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
            if (OutputFormat.XML.equals(requestModel.outputFormat))
            {
                transformAndSerializeOutputXml(response, result, !requestModel.clustersOnly, true);
            }
            else if (OutputFormat.JSON.equals(requestModel.outputFormat))
            {
                response.setContentType(MIME_JSON_UTF8);
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
     * Serializes the result as XML, optionally applying the configured XSLT
     * transformation.
     */
    private void transformAndSerializeOutputXml(HttpServletResponse response,
        ProcessingResult result, boolean includeDocuments, boolean includeClusters)
        throws Exception, IOException
    {
        response.setContentType(MIME_XML_UTF8);
        if (xsltTemplates != null)
        {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            result.serialize(output, includeDocuments, includeClusters);
            xsltTemplates.newTransformer().transform(
                new StreamSource(new ByteArrayInputStream(output.toByteArray())),
                new StreamResult(response.getOutputStream()));

        }
        else
        {
            result.serialize(response.getOutputStream(), includeDocuments,
                includeClusters);
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
        config.logger.error(finalMessage, e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, finalMessage);
    }

    private void sendBadRequest(String message, HttpServletResponse response, Throwable e)
        throws IOException
    {
        final String finalMessage = message + ": " + e.getMessage();
        config.logger.error(finalMessage);
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

        appender.setEncoding(UTF8);
        appender.setImmediateFlush(true);

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
            EXAMPLE_INPUT = ProcessingResult.deserialize(streamInput);
            streamOutput = new ClassResource(RestProcessorServlet.class,
                "example-output.xml").open();
            EXAMPLE_OUTPUT = ProcessingResult.deserialize(streamOutput);
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
