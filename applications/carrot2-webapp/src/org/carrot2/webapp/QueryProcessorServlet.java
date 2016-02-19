
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

package org.carrot2.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.ControllerStatistics;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.source.etools.IpBannedException;
import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.AttributeBinder.IAttributeTransformer;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.resource.IResourceLocator;
import org.carrot2.util.resource.PrefixDecoratorLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.resource.ServletContextLocator;
import org.carrot2.webapp.jawr.JawrUrlGenerator;
import org.carrot2.webapp.model.AttributeMetadataModel;
import org.carrot2.webapp.model.ModelWithDefault;
import org.carrot2.webapp.model.PageModel;
import org.carrot2.webapp.model.RequestModel;
import org.carrot2.webapp.model.RequestType;
import org.carrot2.webapp.model.ResultsCacheModel;
import org.carrot2.webapp.model.ResultsSizeModel;
import org.carrot2.webapp.model.ResultsViewModel;
import org.carrot2.webapp.model.SkinModel;
import org.carrot2.webapp.model.WebappConfig;
import org.carrot2.webapp.util.UserAgentUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;
import org.slf4j.Logger;

import org.carrot2.shaded.guava.common.collect.ImmutableMap;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Processes search requests.
 */
@SuppressWarnings("serial")
public class QueryProcessorServlet extends HttpServlet
{
    /** System property to enable class path search for resources in tests. */
    public final static String ENABLE_CLASSPATH_LOCATOR = "enable.classpath.locator";
    
    /** Controller that performs all searches */
    private transient Controller controller;

    /** Generates urls to combined CSS/Javascript files */
    private transient JawrUrlGenerator jawrUrlGenerator;

    /** {@link #queryLogger} name. */
    static final String QUERY_LOG_NAME = "queryLog";

    /** Query logger. */
    private transient volatile Logger queryLogger = org.slf4j.LoggerFactory.getLogger(QUERY_LOG_NAME);

    /** Error log */
    private transient volatile Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    /** A reference to custom log appenders. */
    private transient volatile LogInitContextListener logInitializer;

    /** Global configuration. */
    private WebappConfig webappConfig;

    /** @see UnknownToDefaultTransformer */
    private UnknownToDefaultTransformer unknownToDefaultTransformer;
    private UnknownToDefaultTransformer unknownToDefaultTransformerWithMaxResults;

    /**
     * Define this system property to enable statistical information from the query
     * processor. A GET request to {@link QueryProcessorServlet} with parameter
     * <code>type=STATS</code> and <code>stats.key</code> equal to the value of this
     * property will return plain text information about the processing state.
     */
    public final static String STATS_KEY = "stats.key";

    /** Response constants */
    private final static String UTF8 = "UTF-8";
    private final static String MIME_XML_UTF8 = "text/xml; charset=" + UTF8;
    private final static String MIME_TEXT_PLAIN_UTF8 = "text/plain; charset=" + UTF8;

    /*
     * Servlet lifecycle.
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        final ServletContext servletContext = config.getServletContext();

        /*
         * If initialized, custom logging initializer will be here. Save its reference for
         * deferred initialization (for servlet APIs < 2.5).
         */
        logInitializer = (LogInitContextListener) servletContext
            .getAttribute(LogInitContextListener.CONTEXT_ID);

        /*
         * Initialize global configuration and publish it.
         */
        this.webappConfig = WebappConfig.getSingleton(servletContext);
        this.unknownToDefaultTransformer = new UnknownToDefaultTransformer(webappConfig, false);
        this.unknownToDefaultTransformerWithMaxResults = new UnknownToDefaultTransformer(webappConfig, true);

        /*
         * Initialize the controller.
         */
        List<IResourceLocator> locators = Lists.newArrayList();
        locators.add(new PrefixDecoratorLocator(
            new ServletContextLocator(getServletContext()), "/WEB-INF/resources/"));

        if (Boolean.getBoolean(ENABLE_CLASSPATH_LOCATOR))
            locators.add(Location.CONTEXT_CLASS_LOADER.locator);

        controller = ControllerFactory.createCachingPooling(
            ResultsCacheModel.toClassArray(webappConfig.caches));
        controller.init(
            ImmutableMap.<String, Object> of(
                AttributeUtils.getKey(DefaultLexicalDataFactory.class, "resourceLookup"),
                new ResourceLookup(locators)),
            webappConfig.components.getComponentConfigurations());

        jawrUrlGenerator = new JawrUrlGenerator(servletContext);
    }

    /*
     * Servlet lifecycle.
     */
    @Override
    public void destroy()
    {
        if (this.controller != null)
        {
            this.controller.dispose();
            this.controller = null;
        }

        this.jawrUrlGenerator = null;
        this.queryLogger = null;

        super.destroy();
    }

    /*
     * Perform GET request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        /*
         * Lots of people still use Tomcat 5.5. which has Servlet 2.4 API. Deferred
         * initialization with the now-available context path is here.
         */
        if (logInitializer != null)
        {
            synchronized (this.getClass())
            {
                // Double locking, but the variable is volatile, so o.k.
                if (logInitializer != null)
                {
                    logInitializer.addAppenders(request.getContextPath());
                }
                logInitializer = null;
            }
        }
    
        // Unpack parameters from string arrays
        final Map<String, Object> requestParameters;
        try {
            requestParameters = MapUtils.unpack(request.getParameterMap());
        } catch (Exception e) {
            logger.info("Skipping, could not parse parameters: " + e.toString());
            return;
        }
    
        // Alias "q" to "query" parameter
        final String queryFromAlias = 
            (String) requestParameters.get(WebappConfig.QUERY_PARAM_ALIAS);
        if (StringUtils.isNotBlank(queryFromAlias))
        {
            requestParameters.put(WebappConfig.QUERY_PARAM, queryFromAlias);
        }

        // Remove query if blank. This will get the user back to the startup screen.
        final String query = (String) requestParameters.get(WebappConfig.QUERY_PARAM);
        if (StringUtils.isBlank(query))
        {
            requestParameters.remove(WebappConfig.QUERY_PARAM);
        }
        else
        {
            requestParameters.put(WebappConfig.QUERY_PARAM, query.trim());
        }

        final RequestModel requestModel;
        try
        {
            // Build model for this request
            requestModel = new RequestModel(webappConfig);
            requestModel.modern = UserAgentUtils.isModernBrowser(request);
            
            // Request type is normally bound to the model, but we need to know
            // the type before binding to choose the unknown values resolution strategy
            final String requestType = (String)requestParameters.get(WebappConfig.TYPE_PARAM);
            
            final AttributeBinder.AttributeBinderActionBind attributeBinderActionBind = 
                new AttributeBinder.AttributeBinderActionBind(
                    requestParameters,
                    true,
                    AttributeBinder.AttributeTransformerFromString.INSTANCE,
                    RequestType.CARROT2DOCUMENTS.name().equals(requestType) ? 
                        unknownToDefaultTransformerWithMaxResults : 
                        unknownToDefaultTransformer);
            AttributeBinder.bind(requestModel,
                new AttributeBinder.IAttributeBinderAction []
                {
                    attributeBinderActionBind
                }, Input.class);
            requestModel.afterParametersBound(attributeBinderActionBind.remainingValues,
                extractCookies(request));
        }
        catch (Exception e)
        {
            logger.info("Skipping, could not map/bind request model attributes: "
                + e.toString());
            return;
        }

        try
        {
            switch (requestModel.type)
            {
                case STATS:
                    handleStatsRequest(request, response, requestParameters, requestModel);
                    break;

                case ATTRIBUTES:
                    handleAttributesRequest(request, response, requestParameters,
                        requestModel);
                    break;

                case SOURCES:
                    handleSourcesRequest(request, response, requestParameters,
                        requestModel);
                    break;

                default:
                    handleSearchRequest(request, response, requestParameters,
                        requestModel);
            }
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    /**
     * Handles requests for document source attributes.
     * 
     * @throws Exception
     */
    private void handleAttributesRequest(HttpServletRequest request,
        HttpServletResponse response, Map<String, Object> requestParameters,
        RequestModel requestModel) throws Exception
    {
        response.setContentType(MIME_XML_UTF8);
        final AjaxAttributesModel model = new AjaxAttributesModel(webappConfig, requestModel);

        final Persister persister = new Persister(getPersisterFormat(requestModel));
        persister.write(model, response.getWriter());
        setExpires(response, 60 * 24 * 7); // 1 week
    }

    /**
     * Required for serialization of attribute metadata combined with a request model.
     */
    @Root(name = "ajax-attribute-metadata")
    private static class AjaxAttributesModel
    {
        @Element(name = "request")
        public final RequestModel requestModel;

        @Element(name = "attribute-metadata")
        public final AttributeMetadataModel attributesModel;

        private AjaxAttributesModel(WebappConfig config, RequestModel requestModel)
        {
            this.requestModel = requestModel;
            this.attributesModel = new AttributeMetadataModel(config);
        }
    }

    /**
     * Handles list of sources requests.
     */
    private void handleSourcesRequest(HttpServletRequest request,
        HttpServletResponse response, Map<String, Object> requestParameters,
        RequestModel requestModel) throws Exception
    {
        response.setContentType(MIME_XML_UTF8);
        final PageModel pageModel = new PageModel(webappConfig, request, requestModel,
            jawrUrlGenerator, null, null);

        final Persister persister = new Persister(
            getPersisterFormat(pageModel.requestModel));
        persister.write(pageModel, response.getWriter());
    }

    /**
     * Handles controller statistics requests.
     */
    private void handleStatsRequest(HttpServletRequest request,
        HttpServletResponse response, Map<String, Object> requestParameters,
        RequestModel requestModel) throws IOException
    {
        final String key = System.getProperty(STATS_KEY);
        if (key != null && key.equals(requestModel.statsKey))
        {
            final ControllerStatistics statistics = controller.getStatistics();

            // Sets encoding for the response writer
            response.setContentType(MIME_TEXT_PLAIN_UTF8);
            final Writer output = response.getWriter();

            output.write("clustering-total-queries: " + statistics.totalQueries + "\n");
            output.write("clustering-good-queries: " + statistics.goodQueries + "\n");

            if (statistics.algorithmTimeAverageInWindow > 0)
            {
                output.write("clustering-ms-per-query: "
                    + statistics.algorithmTimeAverageInWindow + "\n");
                output.write("clustering-updates-in-window: "
                    + statistics.algorithmTimeMeasurementsInWindow + "\n");
            }

            if (statistics.sourceTimeAverageInWindow > 0)
            {
                output.write("source-ms-per-query: "
                    + statistics.sourceTimeAverageInWindow + "\n");
                output.write("source-updates-in-window: "
                    + statistics.sourceTimeMeasurementsInWindow + "\n");
            }

            if (statistics.totalTimeAverageInWindow > 0)
            {
                output.write("all-ms-per-query: " + statistics.totalTimeAverageInWindow + "\n");
                output.write("all-updates-in-window: "
                    + statistics.totalTimeMeasurementsInWindow + "\n");
            }

            output.write("jvm.freemem: " + Runtime.getRuntime().freeMemory() + "\n");
            output.write("jvm.totalmem: " + Runtime.getRuntime().totalMemory() + "\n");

            output.write("cache.hits: " + statistics.cacheHitsTotal + "\n");
            output.write("cache.misses: " + statistics.cacheMisses + "\n");

            output.flush();
        }
        else
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Handles search requests.
     */
    private void handleSearchRequest(HttpServletRequest request,
        HttpServletResponse response, final Map<String, Object> requestParameters,
        final RequestModel requestModel) throws IOException, Exception
    {
        // Add some values in case there was no query parameters -- we want to use the
        // web application defaults and not component defaults.
        requestParameters.put(AttributeNames.RESULTS, requestModel.results);

        // Remove values corresponding to internal attributes
        requestParameters.keySet().removeAll(
            webappConfig.componentInternalAttributeKeys);

        // Perform processing
        ProcessingResult processingResult = null;
        ProcessingException processingException = null;
        try
        {
            if (requestModel.type.requiresProcessing)
            {
                switch (requestModel.type)
                {
                    case CLUSTERS:
                    case FULL:
                    case CARROT2:
                        logQuery(true, requestModel, null);
                        processingResult = controller.process(requestParameters,
                            requestModel.source, requestModel.algorithm);
                        logQuery(false, requestModel, processingResult);
                        break;

                    case DOCUMENTS:
                        processingResult = controller.process(requestParameters,
                            requestModel.source, 
                            webappConfig.QUERY_HIGHLIGHTER_ID);
                        break;

                    case CARROT2DOCUMENTS:
                        processingResult = controller.process(requestParameters,
                            requestModel.source);
                        break;

                    default:
                        throw new RuntimeException("Should not reach here.");
                }

                setExpires(response, 5);
            }
        }
        catch (ProcessingException e)
        {
            processingException = e;

            if (e.getCause() instanceof IpBannedException)
            {
                logger.info("Skipping, source IP banned: " + request.getRemoteAddr());
            }
            else
            {
                logger.error("Processing error: " + e.getMessage(), e);
            }
        }

        // Send response, sets encoding of the response writer.
        response.setContentType(MIME_XML_UTF8);

        final Persister persister = new Persister(getPersisterFormat(requestModel));
        final PrintWriter writer = response.getWriter();
        if (RequestType.CARROT2.equals(requestModel.type) || 
            RequestType.CARROT2DOCUMENTS.equals(requestModel.type))
        {
            // Check for an empty processing result.
            if (processingException != null)
            {
                response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error: " + processingException.getMessage());
                return;
            }

            persister.write(processingResult, writer);
        }
        else
        {
            response.setContentType(MIME_XML_UTF8);

            final PageModel pageModel = new PageModel(webappConfig, request, requestModel,
                jawrUrlGenerator, processingResult, processingException);

            persister.write(pageModel, writer);
        }
    }

    private void logQuery(boolean debug, RequestModel requestModel,
        ProcessingResult processingResult)
    {
        if (debug && !queryLogger.isDebugEnabled()) return;
        if (!queryLogger.isInfoEnabled()) return;

        final String message = requestModel.algorithm
            + ","
            + requestModel.source
            + ","
            + requestModel.results
            + ","
            + (processingResult == null ? "-" : processingResult.getAttributes().get(
                AttributeNames.PROCESSING_TIME_TOTAL)) + "," + requestModel.query;

        if (debug) 
            queryLogger.debug(message); 
        else 
            queryLogger.info(message);
    }

    private void setExpires(HttpServletResponse response, int minutes)
    {
        final HttpServletResponse httpResponse = response;

        final Calendar expiresCalendar = Calendar.getInstance();
        expiresCalendar.add(Calendar.MINUTE, minutes);
        httpResponse.addDateHeader("Expires", expiresCalendar.getTimeInMillis());
    }

    private Format getPersisterFormat(RequestModel requestModel)
    {
        return new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<?ext-stylesheet resource=\""
            + webappConfig.getContextRelativeSkinStylesheet(requestModel) + "\" ?>");
    }

    private Map<String, String> extractCookies(HttpServletRequest request)
    {
        final Map<String, String> result = Maps.newHashMap();
        final Cookie [] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                result.put(cookie.getName(), cookie.getValue());
            }
        }

        return result;
    }

    /**
     * A transformer that replaces unknown source, document, skin, results and view values
     * to default values.
     */
    private static class UnknownToDefaultTransformer implements IAttributeTransformer
    {
        private final Map<String, Collection<?>> knownValues;
        private final Map<String, Object> defaultValues;

        private final boolean useMaxCarrot2Results;
        private final Integer maxCarrot2Results;
        
        public UnknownToDefaultTransformer(WebappConfig config, boolean useMaxCarrot2Results)
        {
            knownValues = Maps.newHashMap();
            defaultValues = Maps.newHashMap();
            
            this.maxCarrot2Results = config.maxCarrot2Results;
            this.useMaxCarrot2Results = useMaxCarrot2Results;

            // Result sizes
            final Set<Integer> resultSizes = Sets.newHashSet();
            for (ResultsSizeModel size : config.sizes)
            {
                resultSizes.add(size.size);
            }
            knownValues.put(WebappConfig.RESULTS_PARAM, resultSizes);
            defaultValues.put(WebappConfig.RESULTS_PARAM, ModelWithDefault.getDefault(config.sizes).size);

            // Skins
            final Set<String> skinIds = Sets.newHashSet();
            for (SkinModel skin : config.skins)
            {
                skinIds.add(skin.id);
            }
            knownValues.put(WebappConfig.SKIN_PARAM, skinIds);
            defaultValues.put(WebappConfig.SKIN_PARAM, ModelWithDefault.getDefault(config.skins).id);

            // Views
            final Set<String> viewIds = Sets.newHashSet();
            for (ResultsViewModel view : config.views)
            {
                viewIds.add(view.id);
            }
            knownValues.put(WebappConfig.VIEW_PARAM, viewIds);
            defaultValues.put(WebappConfig.VIEW_PARAM, ModelWithDefault.getDefault(config.views).id);

            // Sources
            knownValues.put(WebappConfig.SOURCE_PARAM,
                config.sourceAttributeMetadata.keySet());
            defaultValues.put(WebappConfig.SOURCE_PARAM, config.components.getSources().get(0).getId());

            // Algorithms
            knownValues
                .put(WebappConfig.ALGORITHM_PARAM,
                    Lists.transform(
                            config.components.getAlgorithms(),
                            ProcessingComponentDescriptor.ProcessingComponentDescriptorToId.INSTANCE));
            defaultValues.put(WebappConfig.ALGORITHM_PARAM,
                config.components.getAlgorithms().get(0).getId());
        }

        public Object transform(Object value, String key, Field field)
        {
            final Object defaultValue = defaultValues.get(key);

            if (maxCarrot2Results != null && useMaxCarrot2Results
                && WebappConfig.RESULTS_PARAM.equals(key))
            {
                if (value == null)
                {
                    return defaultValues.get(key);
                }
                
                // Just check if the requested number of results is smaller than
                // the maximum configured for this instance of the webapp
                if (((Integer) value) <= maxCarrot2Results)
                {
                    return value;
                }
                else
                {
                    return maxCarrot2Results;
                }
            }
            
            // Check if we want to handle this attribute at all
            if (defaultValue != null)
            {
                if (knownValues.get(key).contains(value))
                {
                    return value;
                }
                else
                {
                    return defaultValue;
                }
            }
            else
            {
                return value;
            }
        }
    }
}
