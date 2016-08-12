
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
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
import org.carrot2.core.ProcessingComponentConfiguration;
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
import org.carrot2.util.resource.DirLocator;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.IResourceLocator;
import org.carrot2.util.resource.PrefixDecoratorLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.resource.ServletContextLocator;
import org.carrot2.util.xslt.NopURIResolver;

import org.carrot2.shaded.guava.common.collect.ImmutableMap;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A servlet that parses HTTP POST input in Carrot<sup>2</sup> XML format, clusters it and
 * returns clusters.
 */
@SuppressWarnings("serial")
public final class RestProcessorServlet extends HttpServlet
{
    /**
     * C2 stream parameter.
     */
    private static final String DCS_C2STREAM = "dcs.c2stream";

    /** System property to disable log file appender. */
    final static String DISABLE_LOGFILE_APPENDER = "disable.logfile";

    /** System property to enable class path search for resources in tests. */
    final static String ENABLE_CLASSPATH_LOCATOR = "enable.classpath.locator";

    /** Response constants */
    private final static String UTF8 = "UTF-8";
    private final static String MIME_XML_UTF8 = "text/xml; charset=" + UTF8;
    private final static String MIME_JSON_UTF8 = "text/json; charset=" + UTF8;

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

    private transient DcsConfig config;

    private transient ProcessingComponentSuite componentSuite;

    private transient Controller controller;

    private String defaultAlgorithmId;

    private transient Templates xsltTemplates;

    /**
     * Disable log file appender configured in {@link #getLogAppender(HttpServletRequest)}
     * . The appender is enabled by default, but disabled for tests.
     */
    private boolean disableLogFileAppender = Boolean.getBoolean(DISABLE_LOGFILE_APPENDER);

    /**
     * Handle a GET command.
     */
    private abstract class CommandAction
    {
        public abstract void handle(HttpServletRequest request, HttpServletResponse response)
            throws Exception;
    };

    private transient HashMap<String, CommandAction> commandActions = new HashMap<String, CommandAction>() {{
        put("components", new CommandAction() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
            {
                response.setContentType(MIME_XML_UTF8);
                componentSuite.serialize(response.getOutputStream());
            }
        });
        put("input-example", new CommandAction() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
            {
                response.setContentType(MIME_XML_UTF8);
                EXAMPLE_INPUT.serialize(response.getOutputStream(), true, false);
            }
        });
        put("output-example-xml", new CommandAction() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
            {
                transformAndSerializeOutputXml(response, EXAMPLE_OUTPUT, true, true);
            }
        });
        put("output-example-json", new CommandAction() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
            {
                response.setContentType(MIME_JSON_UTF8);
                EXAMPLE_OUTPUT.serializeJson(response.getWriter(), null, true, true, true);
            }
        });
        put("status", new CommandAction() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
            {
                response.setContentType(MIME_XML_UTF8);
                controller.getStatistics().serialize(response.getOutputStream());
            }
        });

        // Aliases for clustering commands.
        CommandAction clusteringAction = new CommandAction() {
            public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception
            {
                handleWwwUrlEncoded(request, response);
            }
        };
        put("rest", clusteringAction);
        put("cluster", clusteringAction);
    }};

    @SuppressWarnings("unchecked")
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        if (!disableLogFileAppender)
        {
            try {
              Logger.getRootLogger().addAppender(getLogAppender(servletConfig.getServletContext()));
            } catch (IOException e) {
              throw new ServletException(e);
            }
        }

        // Run in servlet container, load config from config.xml.
        ResourceLookup webInfLookup = new ResourceLookup(new PrefixDecoratorLocator(
            new ServletContextLocator(getServletContext()), "/WEB-INF/"));

        try
        {
            config = DcsConfig.deserialize(webInfLookup.getFirst("dcs-config.xml"));
        }
        catch (Exception e)
        {
            throw new ServletException("Could not read 'config.xml' resource.", e);
        }

        config.logger.debug("DCS request processor starting.");        

        // Initialize XSLT
        initXslt(config, webInfLookup);

        // Load component suite. Use classpath too (for JUnit tests).
        try
        {
            List<IResourceLocator> resourceLocators = Lists.newArrayList();
            resourceLocators.add(new PrefixDecoratorLocator(new ServletContextLocator(
                getServletContext()), "/WEB-INF/suites/"));

            if (Boolean.getBoolean(ENABLE_CLASSPATH_LOCATOR)) resourceLocators
                .add(Location.CONTEXT_CLASS_LOADER.locator);

            ResourceLookup suitesLookup = new ResourceLookup(resourceLocators);

            IResource suiteResource = suitesLookup
                .getFirst(config.componentSuiteResource);
            if (suiteResource == null)
            {
                throw new Exception(
                    "Suite file not found in servlet context's /WEB-INF/suites: "
                        + config.componentSuiteResource);
            }
            componentSuite = ProcessingComponentSuite.deserialize(suiteResource,
                suitesLookup);
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

        controller = ControllerFactory.createCachingPooling(
            cachedComponentClasses.toArray(new Class [cachedComponentClasses.size()]));

        List<IResourceLocator> locators = Lists.newArrayList();
        locators.add(new PrefixDecoratorLocator(new ServletContextLocator(
            getServletContext()), "/WEB-INF/resources/"));

        if (Boolean.getBoolean(ENABLE_CLASSPATH_LOCATOR)) locators
            .add(Location.CONTEXT_CLASS_LOADER.locator);

        // Allow multiple resource lookup paths for different component configurations.
        String resourceLookupAttrKey = AttributeUtils.getKey(DefaultLexicalDataFactory.class, "resourceLookup");
        String altResourceLookupAttrKey = "dcs.resource-lookup";
        ProcessingComponentConfiguration [] configurations = componentSuite.getComponentConfigurations();
        for (int i = 0; i < configurations.length; i++) {
            ProcessingComponentConfiguration config = configurations[i];
            Object location = config.attributes.get(altResourceLookupAttrKey);
            if (location != null && location instanceof String) {
                File resourceDir = new File((String) location);
                if (!resourceDir.isDirectory()) {
                    Logger.getRootLogger().warn("Not a resource folder, ignored: " + resourceDir);
                } else {
                    HashMap<String,Object> mutableMap = new HashMap<String,Object>(config.attributes);
                    mutableMap.put(resourceLookupAttrKey,
                        new ResourceLookup(new DirLocator(resourceDir)));
                    config = configurations[i] = new ProcessingComponentConfiguration(
                        config.componentClass,
                        config.componentId,
                        mutableMap);
                }
            }
        }

        controller.init(
            ImmutableMap.<String, Object> of(resourceLookupAttrKey, new ResourceLookup(locators)), 
            configurations);

        config.logger.info("DCS request processor started.");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Allow ajax requests from anywhere. This is respected by browsers only 
        // anyway and somebody installing the DCS should provide other authentication/ filtering
        // means to limit potential spam/ leechers.
        response.setHeader("Access-Control-Allow-Origin", "*");

        super.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final String command = getCommandName(request);
        if (!StringUtils.isEmpty(command))
        {
            if (!commandActions.containsKey(command))
            {
                sendBadRequest("No such command: " + command, response, null);
                return;
            }

            try
            {
                commandActions.get(command).handle(request, response);
            } 
            catch (Exception e)
            {
                sendInternalServerError("Internal error when processing command: "
                    + command, response, e);
                return;
            }
        }
        else
        {
            // If no command given, assume a clustering request.
            handleWwwUrlEncoded(request, response);
        }
    }

    /**
     * Handle REST requests (HTTP POST with multipart/form-data content).
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        if (ServletFileUpload.isMultipartContent(request))
        {
            handleMultiPart(request, response);
        }
        else
        {
            handleWwwUrlEncoded(request, response);
        }
    }

    /**
     * Handle <tt>www-url-encoded</tt> parameters from GET or POST requests. GET will not support
     * <tt>dcs.c2stream</tt> parameter.
     */
    private void handleWwwUrlEncoded(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        // Don't allow GET/dcs.c2stream combination.
        if (request.getMethod().equalsIgnoreCase("GET") &&
            request.getParameter(DCS_C2STREAM) != null)
        {
            sendBadRequest("dcs.c2stream only supported in POST requests.", response, null);
            return;
        }

        // Check for c2stream in a POST/www-url-encoded and decode it... or try to.
        ProcessingResult input = null;
        if (request.getMethod().equalsIgnoreCase("POST") &&
            request.getParameter(DCS_C2STREAM) != null)
        {
            // Deserialize documents from the stream
            try
            {
                input = ProcessingResult.deserialize(request.getParameter(DCS_C2STREAM));
            }
            catch (Exception e)
            {
                config.logger.error("Trying to parse: " + request.getParameter(DCS_C2STREAM));
                sendBadRequest("Could not parse Carrot2 XML stream", response, e);
                return;
            }
        }

        // Everything else is identical for POST and GET.
        final Map<String, Object> parameters = Maps.newHashMap();
        @SuppressWarnings("unchecked")
        final Enumeration<String> parameterNames = (Enumeration<String>) request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            final String key = parameterNames.nextElement();
            if (DCS_C2STREAM.equals(key))
            {
                continue;
            }
            parameters.put(key, request.getParameter(key));
        }
        processRequest(response, input, parameters);
    }

    /**
     * Handle multipart request, possibly including dcs.c2stream. 
     */
    @SuppressWarnings("unchecked")
    private void handleMultiPart(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        final Map<String, Object> parameters = Maps.newHashMap();
        ProcessingResult input = null;

        final ServletFileUpload upload = new ServletFileUpload(new MemoryFileItemFactory());
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
        for (FileItem fileItem : items)
        {
            final String fieldName = fileItem.getFieldName();
            if (DCS_C2STREAM.equals(fieldName))
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
            }
            else if (fileItem.isFormField())
            {
                parameters.put(fieldName, fileItem.getString());
            }
        }

        processRequest(response, input, parameters);
    }

    /**
     * Process the clustering request.
     * 
     * @param input {@link ProcessingResult}, if any available in the request.
     * @param parameters
     * @throws IOException
     */
    private void processRequest(HttpServletResponse response, 
        ProcessingResult input, final Map<String, Object> parameters) 
        throws IOException
    {
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
                new AttributeBinder.IAttributeBinderAction [] { attributeBinderActionBind }, 
                Input.class);
        }
        catch (Exception bindingException)
        {
            sendInternalServerError("Could not bind request parameters", response,
                bindingException);
            return;
        }

        // Build the attributes used for processing. Use the ones defined in the input
        // XML, if any, and override with the ones provided in POST parameters.
        final Map<String, Object> processingAttributes = Maps.newHashMap();

        // Attributes from the XML stream
        if (input != null)
        {
            processingAttributes.putAll(input.getAttributes());
        }
        
        // Attributes provided in the POST parameters
        processingAttributes.putAll(attributeBinderActionBind.remainingValues);

        if (StringUtils.isEmpty(requestModel.algorithm))
        {
            requestModel.algorithm = defaultAlgorithmId;
        }

        // We need either sourceId or direct document feed
        List<Document> documents = (input != null ? input.getDocuments() : null);
        if (requestModel.source == null && documents == null)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Either dcs.source or a non-empty document list in dcs.c2stream must be provided");
            return;
        }

        // Perform processing
        ProcessingResult result = null;
        try
        {
            long start = System.currentTimeMillis();
            if (requestModel.source != null)
            {
                result = controller.process(processingAttributes, requestModel.source, requestModel.algorithm);
            }
            else
            {
                result = controller.process(processingAttributes, requestModel.algorithm);
            }

            if (config.logger.isInfoEnabled()) {
              config.logger.info(String.format(Locale.ROOT,
                  "Processed %d documents (~%.2f KB) from %s using %s [%.2fs.]",
                  result.getDocuments().size(),
                  approximateCharacterCount(result.getDocuments()) / 1024d,
                  requestModel.source == null ? "[request]" : requestModel.source,
                  requestModel.algorithm,
                  (System.currentTimeMillis() - start) / 1000.0));
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
                transformAndSerializeOutputXml(response, result,
                    !requestModel.clustersOnly, true);
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

    private long approximateCharacterCount(List<Document> documents) {
      long size = 0;
      for (Document doc : documents) {
        size += sizeOf(doc.getTitle());
        size += sizeOf(doc.getSummary());
      }
      return size;
    }

    private long sizeOf(String string) {
      return string == null ? 0 : string.length();
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
     * Command name is the last component of the request URI.
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
        final String finalMessage = message + 
            (e != null ? ": " + e.getMessage() : "");
        config.logger.error(finalMessage);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, finalMessage);
    }

    private FileAppender getLogAppender(ServletContext context) throws IOException
    {
        String contextPath = context.getContextPath();
        if (StringUtils.isBlank(contextPath))
        {
            contextPath = "root";
        }

        contextPath = contextPath.replaceAll("[^a-zA-Z0-9\\-]", "");
        final String catalinaHome = System.getProperty("catalina.home");
        final File logPrefix = new File(catalinaHome != null ? catalinaHome + "/logs" : "logs");
        if (!logPrefix.isDirectory()) {
            logPrefix.mkdirs();
        }
        
        String logDestination = new File(logPrefix, "/c2-dcs-" + contextPath + "-full.log").getAbsolutePath();
        final FileAppender appender = 
            new FileAppender(new PatternLayout("%d{ISO8601} [%-5p] [%c] %m%n"), logDestination, true);

        appender.setEncoding(UTF8);
        appender.setImmediateFlush(true);

        return appender;
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
    public void destroy()
    {
        if (this.controller != null)
        {
            this.controller.dispose();
            this.controller = null;
        }
    
        super.destroy();
    }
}
