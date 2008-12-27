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

package org.carrot2.webapp;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;
import org.carrot2.webapp.filter.QueryWordHighlighter;
import org.carrot2.webapp.jawr.JawrUrlGenerator;
import org.carrot2.webapp.model.*;
import org.carrot2.webapp.util.UserAgentUtils;
import org.simpleframework.xml.load.Persister;
import org.simpleframework.xml.stream.Format;

import com.google.common.collect.Maps;

/**
 * Processes search requests.
 */
public class QueryProcessorServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    /** Controller that performs all searches */
    private transient CachingController controller;

    /** Generates urls to combined CSS/Javascript files */
    private transient JawrUrlGenerator jawrUrlGenerator;

    /** {@link #queryLogger} name. */
    static final String QUERY_LOG_NAME = "queryLog";

    /** Query logger. */
    private transient volatile Logger queryLogger = Logger.getLogger(QUERY_LOG_NAME);

    /** Error log */
    private transient volatile Logger logger = Logger.getLogger(getClass());

    /** A reference to custom log appenders. */
    private transient volatile LogInitContextListener logInitializer;

    /**
     * Define this system property to enable statistical information from the query
     * processor. A GET request to {@link QueryProcessorServlet} with parameter
     * <code>type=STATS</code> and <code>stats.key</code> equal to the value of this
     * property will return plain text information about the processing state.
     */
    public final static String STATS_KEY = "stats.key";

    /** Response constants */
    private final static String MIME_XML = "text/xml";
    private final static String ENCODING_UTF = "utf-8";
    private final static String MIME_XML_CHARSET_UTF = MIME_XML + "; charset="
        + ENCODING_UTF;

    /*
     * Servlet lifecycle.
     */
    @Override
    @SuppressWarnings("unchecked")
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

        controller = new CachingController(IDocumentSource.class);
        controller.init(new HashMap<String, Object>(), WebappConfig.INSTANCE.components);

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
        final Map<String, Object> requestParameters = MapUtils.unpack(request
            .getParameterMap());

        // Alias "q" to "query" parameter
        final String queryFromAlias = (String) requestParameters
            .get(WebappConfig.QUERY_PARAM_ALIAS);
        if (StringUtils.isNotBlank(queryFromAlias))
        {
            requestParameters.put(WebappConfig.QUERY_PARAM, queryFromAlias);
        }

        try
        {
            // Build model for this request
            final RequestModel requestModel = WebappConfig.INSTANCE
                .setDefaults(new RequestModel());

            // Special handling for false boolean attributes whose default value is true
            addFalseBooleanParameters(requestParameters
                .containsKey(WebappConfig.SOURCE_PARAM) ? (String) requestParameters
                .get(WebappConfig.SOURCE_PARAM) : requestModel.source, requestParameters);

            requestModel.modern = UserAgentUtils.isModernBrowser(request);
            final AttributeBinder.AttributeBinderActionBind attributeBinderActionBind = new AttributeBinder.AttributeBinderActionBind(
                Input.class, requestParameters, true,
                AttributeBinder.AttributeTransformerFromString.INSTANCE);
            AttributeBinder.bind(requestModel,
                new AttributeBinder.IAttributeBinderAction []
                {
                    attributeBinderActionBind
                }, Input.class);
            requestModel.afterParametersBound(attributeBinderActionBind.remainingValues,
                extractCookies(request));

            if (RequestType.STATS.equals(requestModel.type))
            {
                handleStatsRequest(request, response, requestParameters, requestModel);
            }
            if (RequestType.ATTRIBUTES.equals(requestModel.type))
            {
                handleAttributesRequest(request, response, requestParameters,
                    requestModel);
            }
            else if (RequestType.SOURCES.equals(requestModel.type))
            {
                handleSourcesRequest(request, response, requestParameters, requestModel);
            }
            else
            {
                handleSearchRequest(request, response, requestParameters, requestModel);
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
        response.setContentType(MIME_XML_CHARSET_UTF);
        final AttributeMetadataModel model = new AttributeMetadataModel(requestModel);

        final Persister persister = new Persister(getPersisterFormat(requestModel));
        persister.write(model, response.getWriter());
        setExpires(response, 60 * 24 * 7); // 1 week
    }

    /**
     * Handles list of sources requests.
     */
    private void handleSourcesRequest(HttpServletRequest request,
        HttpServletResponse response, Map<String, Object> requestParameters,
        RequestModel requestModel) throws Exception
    {
        response.setContentType(MIME_XML_CHARSET_UTF);
        final PageModel pageModel = new PageModel(request, requestModel,
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
            final CachingControllerStatistics statistics = controller.getStatistics();

            // Sets encoding for the response writer
            response.setContentType("text/plain; charset=utf-8");
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
                output.write("all-ms-per-query: " + statistics.totalTimeAverageInWindow
                    + "\n");
                output.write("all-updates-in-window: "
                    + statistics.totalTimeMeasurementsInWindow + "\n");
            }

            output.write("jvm.freemem: " + Runtime.getRuntime().freeMemory() + "\n");
            output.write("jvm.totalmem: " + Runtime.getRuntime().totalMemory() + "\n");

            output.write("ehcache.hits: " + statistics.cacheHitsTotal + "\n");
            output.write("ehcache.misses: " + statistics.cacheMisses + "\n");
            output.write("ehcache.memhits: " + statistics.cacheHitsMemory + "\n");
            output.write("ehcache.diskhits: " + statistics.cacheHitsDisk + "\n");

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

        // Perform processing
        ProcessingResult processingResult = null;
        ProcessingException processingException = null;
        try
        {
            if (requestModel.type.requiresProcessing)
            {
                if (RequestType.CLUSTERS.equals(requestModel.type)
                    || RequestType.FULL.equals(requestModel.type)
                    || RequestType.CARROT2.equals(requestModel.type))
                {
                    processingResult = controller.process(requestParameters,
                        requestModel.source, requestModel.algorithm);
                    logQuery(requestModel, processingResult);
                }
                else if (RequestType.DOCUMENTS.equals(requestModel.type))
                {
                    processingResult = controller.process(requestParameters,
                        requestModel.source, QueryWordHighlighter.class.getName());
                }
                setExpires(response, 5);
            }
        }
        catch (ProcessingException e)
        {
            processingException = e;
            logger.error("Processing error: " + e.getMessage(), e);
        }

        // Send response, sets encoding of the response writer.
        response.setContentType(MIME_XML_CHARSET_UTF);
        final PageModel pageModel = new PageModel(request, requestModel,
            jawrUrlGenerator, processingResult, processingException);

        final Persister persister = new Persister(
            getPersisterFormat(pageModel.requestModel));

        if (RequestType.CARROT2.equals(requestModel.type))
        {
            persister.write(processingResult, response.getWriter());
        }
        else
        {
            persister.write(pageModel, response.getWriter());
        }
    }

    private void addFalseBooleanParameters(final String sourceId,
        final Map<String, Object> requestParameters)
    {
        final Collection<String> booleanAttributeKeys = WebappConfig.INSTANCE.sourceBooleanAttributeKeys
            .get(sourceId);
        for (String key : booleanAttributeKeys)
        {
            if (!requestParameters.containsKey(key))
            {
                // If there is no value in HTTP parameters, checkbox was not checked
                // and we need to set the corresponding attribute to false
                requestParameters.put(key, false);
            }
        }
    }

    private void logQuery(RequestModel requestModel, ProcessingResult processingResult)
    {
        this.queryLogger.info(requestModel.algorithm + "," + requestModel.source + ","
            + requestModel.results + ","
            + processingResult.getAttributes().get(AttributeNames.PROCESSING_TIME_TOTAL)
            + "," + requestModel.query);
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
            + "<?xml-stylesheet type=\"text/xsl\" href=\"@"
            + WebappConfig.getContextRelativeSkinStylesheet(requestModel.skin) + "\" ?>");
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
}
