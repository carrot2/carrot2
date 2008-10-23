package org.carrot2.webapp;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;
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

    /** Logger for processed queries */
    private transient volatile Logger queryLogger;

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

        controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap<String, Object>(), WebappConfig.INSTANCE.components);

        jawrUrlGenerator = new JawrUrlGenerator(config.getServletContext());
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
        // TODO: Move me to #init() after upgrading to servlet api 2.5
        synchronized (this)
        {
            if (queryLogger == null)
            {
                queryLogger = Logger.getLogger("queryLog");
                queryLogger.addAppender(getQueryLogAppender(request));
                Logger.getRootLogger().addAppender(getFullLogAppender(request));
            }
        }

        // Unpack parameters from string arrays
        final Map<String, Object> requestParameters = MapUtils.unpack(request
            .getParameterMap());
        try
        {
            // Build model for this request
            final RequestModel requestModel = WebappConfig.INSTANCE
                .setDefaults(new RequestModel());
            requestModel.modern = UserAgentUtils.isModernBrowser(request);
            final AttributeBinder.AttributeBinderActionBind attributeBinderActionBind = new AttributeBinder.AttributeBinderActionBind(
                Input.class, requestParameters, true,
                AttributeBinder.AttributeTransformerFromString.INSTANCE);
            AttributeBinder.bind(requestModel,
                new AttributeBinder.AttributeBinderAction []
                {
                    attributeBinderActionBind
                }, Input.class);
            requestModel.afterParametersBound(attributeBinderActionBind.remainingValues,
                extractCookies(request));

            if (RequestType.STATS.equals(requestModel.type))
            {
                handleStatsRequest(request, response, requestParameters, requestModel);
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

    private FileAppender getFullLogAppender(HttpServletRequest request)
        throws IOException
    {
        final String contextPath = getContextPathSegment(request);
        final String logPrefix = getLogDirPrefix();

        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601},[%p],[%t],%c,%m%n"), logPrefix + "/c2-" + contextPath
            + "-full.log", true);
        appender.setEncoding("UTF-8");
        return appender;
    }

    private FileAppender getQueryLogAppender(HttpServletRequest request)
        throws IOException
    {
        final String contextPath = getContextPathSegment(request);
        final String logPrefix = getLogDirPrefix();
    
        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601},%m%n"), logPrefix + "/c2-" + contextPath + "-queries.log", true);
        appender.setEncoding("UTF-8");
        return appender;
    }

    private String getLogDirPrefix()
    {
        final String catalinaHome = System.getProperty("catalina.home");
        return catalinaHome != null ? catalinaHome + "/logs" : "";
    }

    private String getContextPathSegment(HttpServletRequest request)
    {
        String contextPath = request.getContextPath();
        if (StringUtils.isBlank(contextPath))
        {
            contextPath = "root";
        }
        contextPath = contextPath.replaceAll("[^a-zA-Z0-9\\-]", "");
        return contextPath;
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
                setExpires(response);
            }
        }
        catch (ProcessingException e)
        {
            processingException = e;
        }

        // Send response
        // Sets encoding of the response writer
        response.setContentType(MIME_XML_CHARSET_UTF);
        final PageModel pageModel = new PageModel(request, requestModel,
            jawrUrlGenerator, processingResult, processingException);

        final Persister persister = new Persister(getPersisterFormat(pageModel));

        if (RequestType.CARROT2.equals(requestModel.type))
        {
            persister.write(processingResult, response.getWriter());
        }
        else
        {
            persister.write(pageModel, response.getWriter());
        }
    }

    private void logQuery(RequestModel requestModel, ProcessingResult processingResult)
    {
        this.queryLogger.info(requestModel.algorithm + "," + requestModel.source + ","
            + requestModel.results + ","
            + processingResult.getAttributes().get(AttributeNames.PROCESSING_TIME_TOTAL)
            + "," + requestModel.query);
    }

    private void setExpires(HttpServletResponse response)
    {
        final HttpServletResponse httpResponse = response;

        final Calendar expiresCalendar = Calendar.getInstance();
        expiresCalendar.add(Calendar.MINUTE, 5);
        httpResponse.addDateHeader("Expires", expiresCalendar.getTimeInMillis());
    }

    private Format getPersisterFormat(PageModel pageModel)
    {
        return new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<?xml-stylesheet type=\"text/xsl\" href=\"@"
            + WebappConfig.getContextRelativeSkinStylesheet(pageModel.requestModel.skin)
            + "\" ?>");
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