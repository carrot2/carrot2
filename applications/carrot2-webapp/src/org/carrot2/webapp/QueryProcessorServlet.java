package org.carrot2.webapp;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.simplexml.NoClassAttributePersistenceStrategy;
import org.carrot2.webapp.filter.QueryWordHighlighter;
import org.carrot2.webapp.jawr.JawrUrlGenerator;
import org.carrot2.webapp.model.*;
import org.carrot2.webapp.util.RequestParameterUtils;
import org.carrot2.webapp.util.UserAgentUtils;
import org.simpleframework.xml.load.Persister;
import org.simpleframework.xml.stream.Format;

/**
 * Processes search requests.
 */
@SuppressWarnings("serial")
public class QueryProcessorServlet extends HttpServlet
{
    /** Controller that performs all searches */
    private transient CachingController controller;

    /** Generates urls to combined CSS/Javascript files */
    private transient JawrUrlGenerator jawrUrlGenerator;

    /** Logger for processed queries */
    private volatile Logger queryLogger;

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

    @Override
    @SuppressWarnings("unchecked")
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap<String, Object>(), WebappConfig.INSTANCE.components);

        jawrUrlGenerator = new JawrUrlGenerator(config.getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Initialize query logger.
        // TODO: refactor this to init() and use Servlet API 2.5 to get context
        // path from servlet config
        synchronized (this)
        {
            String contextPath = request.getContextPath();
            if (StringUtils.isBlank(contextPath))
            {
                contextPath = "ROOT";
            }
            contextPath = contextPath.replaceAll("[^a-zA-Z0-9]", "");

            this.queryLogger = Logger.getLogger("queryLog." + contextPath);
        }

        // Unpack parameters from string arrays
        final Map<String, Object> requestParameters = RequestParameterUtils
            .unpack(request);
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
            requestModel.afterParametersBound(attributeBinderActionBind.remainingValues);

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

            response.setContentType("text/plain; charset=utf-8");
            final Writer output = new OutputStreamWriter(response.getOutputStream(),
                "UTF-8");

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
                        requestModel.source, requestModel.algorithm,
                        QueryWordHighlighter.class.getName());
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
        response.setContentType(MIME_XML_CHARSET_UTF);
        final ServletOutputStream outputStream = response.getOutputStream();
        final PageModel pageModel = new PageModel(request, requestModel,
            jawrUrlGenerator, processingResult, processingException);

        final Persister persister = new Persister(
            NoClassAttributePersistenceStrategy.INSTANCE, getPersisterFormat(pageModel));

        if (RequestType.CARROT2.equals(requestModel.type))
        {
            persister.write(processingResult, outputStream);
        }
        else
        {
            persister.write(pageModel, outputStream);
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
}