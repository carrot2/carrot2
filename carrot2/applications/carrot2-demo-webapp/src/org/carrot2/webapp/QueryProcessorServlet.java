
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

package org.carrot2.webapp;

import java.io.*;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import net.sf.ehcache.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayInputComponent;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.util.StringUtils;

/**
 * Query processor servlet.
 * 
 * @author Dawid Weiss
 */
public final class QueryProcessorServlet extends HttpServlet {
    /** Logger for activities and information */
    private Logger logger;

    /** Logger for queries */
    private volatile Logger queryLogger;

    /**
     * Define this system property to enable statistical information from
     * the query processor. A GET request to {@link QueryProcessorServlet}
     * with parameter <code>type=s</code> and <code>key</code> equal
     * to the value of this property will return plain text information
     * about the processing state.
     */
    private final static String STATISTICS_KEY = "stats.key";

    public static final String PARAM_Q = "q";
    public static final String PARAM_INPUT = "in";
    public static final String PARAM_ALG = "alg";
    public static final String PARAM_SIZE = "s";

    private static final int DOCUMENT_REQUEST = 1;
    private static final int CLUSTERS_REQUEST = 2;
    private static final int PAGE_REQUEST = 3;
    private static final int STATS_REQUEST = 4;

    /** All available search settings */
    private SearchSettings searchSettings = new SearchSettings();
    
    /**
     * A map of {@link Broadcaster}s.
     */
    private final HashMap bcasters = new HashMap();

    /**
     * A process controller for input tabs. Each tab's name ({@link TabSearchInput#getShortName()})
     * corresponds to an identifier of a process defined in this controller.
     */
    private LocalControllerBase tabsController; 

    /**
     * A process controller for algorithms. Each algorithm's name ({@link TabAlgorithm#getShortName()})
     * corresponds to an identifier of a process defined in this controller.
     */
    private LocalControllerBase algorithmsController;

    /** Cache of recent queries */
    private Cache ehcache;

    /** Serializer factory used to emit documents and clusters. */
    private SerializersFactory serializerFactory;

    /** A counter for the number of executed queries. */
    private long executedQueries;

    /** Total time spent in clustering routines. */
    private long totalTime;

    /** Total number of successfully processed clustering queries. */
    private long goodQueries;

    /** If <code>true</code> the processes and components have been successfully read. */
    private boolean initialized;

    /**
     * Configure inputs. 
     */
    public void init() throws ServletException {
        this.logger = Logger.getLogger(this.getServletName());

        // Initialize default input size and allowed input sizes.
        int defaultInputSize = 100;
        try {
            defaultInputSize = Integer.parseInt(getServletConfig()
                .getInitParameter("inputSize.default"));
        }
        catch (Exception e){
            logger.warn("Could not parse inputSize.default: " + getServletConfig()
                .getInitParameter("inputSize.default"));
        }        
        searchSettings.setAllowedInputSizes(
                new int [] {50, 100, 200, 400}, defaultInputSize);

        // Run initial process and components configuration.
        try {
            initialize();
        } catch (Throwable t) {
            logger.error("Could not initialize query processor servlet: " + StringUtils.chainExceptionMessages(t), t);
        }
    }

    /**
     * Process a HTTP GET request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        // check if initialized.
        synchronized (this) {
            if (!initialized) {
                // Attempt to repeat initialization procedure.
                logger.info("Repeating initialization.");
                try {
                    initialize();
                } catch (Throwable t) {
                    servletError(request, response, response.getOutputStream(), "Initialization error occurred." , t);
                    return;
                }
                logger.info("Initialization successful.");
            }
        }

        // identify request type first.
        final String type = request.getParameter("type");
        final SearchRequest searchRequest = searchSettings.parseRequest(request.getParameterMap());

        // Initialize loggers depending on the application context.
        if (this.queryLogger == null) {
            synchronized (this) {
                // initialize query logger.
                String contextPath = request.getContextPath();

                contextPath = contextPath.replaceAll("[^a-zA-Z0-9]", "");
                if (contextPath == null || "".equals(contextPath)) {
                    contextPath = "ROOT";
                }

                this.queryLogger = Logger.getLogger("queryLog." + contextPath);
            }
        }

        // Determine request type and redirect control
        final int requestType;
        if ("d".equals(type)) {
            requestType = DOCUMENT_REQUEST;
        } else if ("c".equals(type)) {
            requestType = CLUSTERS_REQUEST;
        } else if ("s".equals(type)) {
            requestType = STATS_REQUEST;
        } else {
            requestType = PAGE_REQUEST;
        }

        final OutputStream os = response.getOutputStream();
        if (requestType == DOCUMENT_REQUEST || requestType == CLUSTERS_REQUEST) {
            // request for documents or clusters
            if (searchRequest.query.length() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            try {
                processSearchQuery(os, requestType, searchRequest, request, response);
            } catch (IOException e) {
                // Handle process exception gracefully?
                servletError(request, response, os, "An internal error occurred." 
                        + searchRequest.getInputTab().getShortName(), e);
            }
        } else if (requestType == STATS_REQUEST) {
            // request for statistical information about the engine.
            // we check if the request contains a special token known to the 
            // administrator of the engine (so that statistics are available
            // only to certain people).
            final String statsKey = System.getProperty(STATISTICS_KEY);
            if (statsKey != null && statsKey.equals(request.getParameter("key"))) {
                synchronized (getServletContext()) {
                    response.setContentType("text/plain; charset=utf-8");
                    final Writer output = new OutputStreamWriter(os, "UTF-8");
    
                    output.write("total-queries: " + executedQueries + "\n");
                    output.write("good-queries: " + goodQueries + "\n");
                    if (goodQueries > 0) {
                        output.write("ms-per-query: " + totalTime / goodQueries + "\n");
                    }

                    output.write("jvm.freemem: " + Runtime.getRuntime().freeMemory() + "\n");
                    output.write("jvm.totalmem: " + Runtime.getRuntime().totalMemory() + "\n");
    
                    final Statistics stats = this.ehcache.getStatistics();
                    output.write("ehcache.hits: " + stats.getCacheHits() + "\n");
                    output.write("ehcache.misses: " + stats.getCacheMisses() + "\n");
                    output.write("ehcache.memhits: " + stats.getInMemoryHits() + "\n");
                    output.write("ehcache.diskhits: " + stats.getOnDiskHits() + "\n");

                    output.flush();
                }
            } else {
                // unauthorized stats request.
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            final PageSerializer serializer = serializerFactory.createPageSerializer(request);
            response.setContentType(serializer.getContentType());
            serializer.writePage(os, searchSettings, searchRequest);
        }
    }

    /**
     * A thread that fetches search results and caches them. 
     */
    private class SearchResultsDownloaderThread extends Thread {
        private final SearchRequest searchRequest;
        private final Broadcaster bcaster;
        private final Serializable queryHash;

        public SearchResultsDownloaderThread(SearchRequest searchRequest, Serializable queryHash, Broadcaster bcaster) {
            this.searchRequest = searchRequest;
            this.bcaster = bcaster;
            this.queryHash = queryHash;
        }

        public void run() {
            final HashMap props = new HashMap();
            props.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, 
                    Integer.toString(searchRequest.getInputSize()));
            props.put(BroadcasterPushOutputComponent.BROADCASTER, bcaster);
            try {
                tabsController.query(searchRequest.getInputTab().getShortName(), searchRequest.query, props);
            } catch (Exception e) {
                logger.warn("Error running input query.", e);
                this.bcaster.endProcessingWithError(e);
                return;
            }

            try {
                // add documents to the cache
                ehcache.put(
                        new net.sf.ehcache.Element(queryHash, 
                                new SearchResults(bcaster.getDocuments())));
            } catch (CacheException e) {
                logger.error("Could not save results to cache.", e);
            }
        }
    }

    /**
     * Process a document or cluster search query.
     */
    private void processSearchQuery(OutputStream os, final int requestType, 
            SearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) 
        throws IOException
    {
        final TabAlgorithm algorithmTab = searchRequest.getAlgorithm();
        final String queryHash = searchRequest.getInputAndSizeHashCode();
        final Broadcaster bcaster;

        // check if we can process this request and return immediately if not.
        if (false == serializerFactory.acceptRequest(request, response)) {
            logger.debug("Request unacceptable (denied by factory).");
            return;
        }

        synchronized (getServletContext()) {
            final Broadcaster existingbcaster = (Broadcaster ) bcasters.get(queryHash); 
            if (existingbcaster != null) {
                //
                // Existing broadcaster is reused.
                //
                bcaster = existingbcaster;
                logger.debug("Broadcaster reused: " + searchRequest.query);
            } else {
                final net.sf.ehcache.Element value = ehcache.get(queryHash);
                if (value != null) {
                    // 
                    // Recreate a broadcaster from the cache.
                    //
                    logger.debug("Broadcaster recovered from cache: " + searchRequest.query);
                    bcaster = new Broadcaster((SearchResults) value.getObjectValue());
                    bcasters.put(queryHash, bcaster);
                } else {
                    //
                    // A new broadcaster is needed.
                    // 
                    logger.debug("Broadcaster created: " + searchRequest.query);
                    bcaster = new Broadcaster();
                    bcasters.put(queryHash, bcaster);
                    // Start a background thread for pulling documents from the input.
                    new SearchResultsDownloaderThread(searchRequest, queryHash, bcaster).start();
                }
            }
            // attach current thread to the broadcaster.
            bcaster.attach();
        }

        long processingTime = -1;
        try {
            final Iterator docIterator = bcaster.docIterator();
            if (requestType == DOCUMENT_REQUEST) {
                //
                // document request
                //
                final RawDocumentsSerializer serializer = serializerFactory.createRawDocumentSerializer(request);
                response.setContentType(serializer.getContentType());
                serializer.startResult(os, searchRequest.query);
                try {
                    while (docIterator.hasNext()) {
                        serializer.write((RawDocument) docIterator.next());
                    }
                } catch (BroadcasterException e) {
                    serializer.processingError(e.getCause());
                }
                serializer.endResult();
            } else {
                //
                // clustering request.
                //
                final HashMap props = new HashMap();
                props.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, docIterator);
                props.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, 
                        Integer.toString(searchRequest.getInputSize()));

                final RawClustersSerializer serializer = serializerFactory.createRawClustersSerializer(request);
                response.setContentType(serializer.getContentType());
                try {
                    logger.info("Clustering results using: " + algorithmTab.getShortName());
                    final long start = System.currentTimeMillis();
                    final ProcessingResult result = 
                        algorithmsController.query(algorithmTab.getShortName(), 
                                searchRequest.query, props);
                    final long stop = System.currentTimeMillis();
                    final ArrayOutputComponent.Result collected =  
                        (ArrayOutputComponent.Result) result.getQueryResult();
                    final List clusters = collected.clusters; 
                    final List documents = bcaster.getDocuments();

                    serializer.startResult(os, documents, request, searchRequest.query);
                    for (Iterator i = clusters.iterator(); i.hasNext();) {
                        serializer.write((RawCluster) i.next());
                    }
                    serializer.endResult();

                    processingTime = stop - start;
                    if (queryLogger.isEnabledFor(Level.INFO)) {
                        logQuery(searchRequest, processingTime);
                    }
                } catch (BroadcasterException e) {
                    // broadcaster exceptions are shown in the documents iframe,
                    // so we simply emit no clusters.
                    serializer.startResult(os, Collections.EMPTY_LIST, request, searchRequest.query);
                    serializer.processingError(e);
                    serializer.endResult();
                } catch (Exception e) {
                    logger.warn("Error running input query.", e);
                    serializer.startResult(os, Collections.EMPTY_LIST, request, searchRequest.query);
                    serializer.processingError(e);
                    serializer.endResult();
                }
            }
        } finally {
            synchronized (getServletContext()) {
                if (requestType == CLUSTERS_REQUEST) {
                    this.executedQueries++;
                    if (processingTime > 0) {
                        this.goodQueries++;
                        this.totalTime += processingTime;
                    }
                }

                // detach current thread from the broadcaster and
                // remove it if necessary.
                bcaster.detach();
                if (!bcaster.inUse()) {
                    bcasters.remove(queryHash);
                    logger.debug("Broadcaster removed: " + searchRequest.query);
                }
            }
        }
    }

    /**
     * Logs the query for further analysis
     */
    private void logQuery(final SearchRequest searchRequest, long clusteringTime) {
        queryLogger.info(
                  searchRequest.getAlgorithm().getShortName()
                + ","
                + searchRequest.getInputTab().getShortName()
                + ","
                + searchRequest.getInputSize()
                + ","
                + clusteringTime
                + ","
                + searchRequest.query);
    }

    /**
     * Initializes components and processes. Sets a flag if successful.
     */
    private void initialize() throws ServletException {
        final ServletContext context = super.getServletContext();

        // Initialize serializers.
        this.serializerFactory = InitializationUtils.initializeSerializers(logger, getServletConfig());

        // Initialize cache.
        this.ehcache = InitializationUtils.initializeCache(getServletConfig());

        // Create processes for collecting documents from input tabs.
        final File inputScripts = new File(context.getRealPath("/inputs"));
        this.tabsController = InitializationUtils.initializeInputs(logger, inputScripts, searchSettings);

        // Create processes for algorithms.
        final File algorithmScripts = new File(context.getRealPath("/algorithms"));
        this.algorithmsController = InitializationUtils.initializeAlgorithms(logger, algorithmScripts, searchSettings);

        // Mark as initialized.
        this.initialized = true;
    }

    /**
     * Attempts to send an internal server error HTTP error, if possible.
     * Otherwise simply pushes the exception message to the output stream. 

     * @param message Message to be printed to the logger and to the output stream.
     * @param t Exception that caused the error.
     */
    protected void servletError(HttpServletRequest origRequest, HttpServletResponse origResponse, OutputStream os, String message, Throwable t) {
        logger.error(message, t);

        final Writer writer;
        try {
            writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
        } catch (UnsupportedEncodingException e) {
            final String msg = Constants.ENCODING_UTF + " must be supported.";
            logger.fatal(msg);
            throw new RuntimeException(msg);
        }

        if (false == origResponse.isCommitted()) {
            // Reset the buffer and previous status code.
            origResponse.reset();
            origResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            origResponse.setContentType(Constants.MIME_HTML_CHARSET_UTF);
        }

        // Response committed. Just push the error to the output stream.
        try {
            writer.write("<h1 style=\"color: red; margin-top: 1em;\">");
            writer.write("Internal server error");
            writer.write("</h1>");
            writer.write("<b>URI</b>: " + origRequest.getRequestURI() + "\n<br/><br/>");
            serializeException(writer, t);
            writer.flush();
        } catch (IOException e) {
            // not much to do in such case (connection broken most likely).
            logger.warn("Exception info could not be returned to client (I/O).");
        }
    }

    /**
     * Utility method to serialize an exception and its stack trace to simple HTML.
     */
    private final static void serializeException(Writer osw, Throwable t) throws IOException {
        Throwable temp = t;
        osw.write("<table border=\"0\" cellspacing=\"4\">");
        while (temp != null) {
            osw.write("<tr>");
            osw.write("<td style=\"text-align: right\">" + (temp != t ? "caused by &rarr;" : "Exception:") + "</td>");
            osw.write("<td style=\"color: gray; font-weight: bold;\">" + temp.getClass().getName() + "</td>");
            osw.write("<td style=\"color: red; font-weight: bold;\">" + (temp.getMessage() != null ? temp.getMessage() : "(no message)") + "</td>");
            osw.write("</tr>");

            if (temp instanceof ServletException) {
                temp = ((ServletException) temp).getRootCause();
            } else {
                temp = temp.getCause();
            }
        }
        osw.write("</table>");

        osw.write("<br/><br/><b>Stack trace:</b>");
        osw.write("<pre style=\"border-left: 1px solid red; padding: 3px; font-family: monospace;\">");
        final PrintWriter pw = new PrintWriter(osw);
        t.printStackTrace(pw);
        pw.flush();
        osw.write("</pre>");
    }
}
