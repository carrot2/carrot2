
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.sf.ehcache.*;

import org.apache.log4j.Logger;
import org.carrot2.webapp.SearchSettings.SearchRequest;
import org.carrot2.webapp.serializers.XMLSerializersFactory;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.*;
import com.dawidweiss.carrot.local.controller.*;
import com.dawidweiss.carrot.local.controller.loaders.BeanShellFactoryDescriptionLoader;

/**
 * Query processor servlet.
 * 
 * @author Dawid Weiss
 */
public final class QueryProcessorServlet extends HttpServlet {
    private Logger logger;

    public static final String PARAM_Q = "q";
    public static final String PARAM_INPUT = "in";
    public static final String PARAM_ALG = "alg";
    public static final String PARAM_SIZE = "s";

    private static final int DOCUMENT_REQUEST = 1;
    private static final int CLUSTERS_REQUEST = 2;
    private static final int PAGE_REQUEST = 3;

    /** All available search settings */
    private SearchSettings searchSettings = new SearchSettings();
    
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

    /**
     * Configure inputs. 
     */
    public void init() throws ServletException {
        this.logger = Logger.getLogger(this.getServletName());
        final ServletContext context = super.getServletContext();

        // Initialize serializers.
        initializeSerializers(getServletConfig());
        
        // Initialize cache.
        initializeCache(getServletConfig());

        // Create processes for collecting documents from input tabs.
        final File inputScripts = new File(context.getRealPath("/inputs"));
        this.tabsController = initializeInputs(inputScripts, searchSettings);

        // Create processes for algorithms.
        final File algorithmScripts = new File(context.getRealPath("/algorithms"));
        this.algorithmsController = initializeAlgorithms(algorithmScripts, searchSettings);

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
    }

    /**
     * Process a HTTP GET request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        // identify request type first.
        final String type = request.getParameter("type");
        final SearchRequest searchRequest = searchSettings.parseRequest(request.getParameterMap());

        final int requestType;
        if ("d".equals(type)) {
            requestType = DOCUMENT_REQUEST;
        } else if ("c".equals(type)) {
            requestType = CLUSTERS_REQUEST;
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

                try {
                    // add documents to the cache
                    ehcache.put(
                            new net.sf.ehcache.Element(queryHash, 
                                    new SearchResults(bcaster.getDocuments())));
                } catch (CacheException e) {
                    logger.error("Could not save results to cache.", e);
                }
            } catch (Exception e) {
                logger.warn("Error running input query.", e);
            }
        }
    }
    
    /**
     * Process a document or cluster search query.
     */
    private void processSearchQuery(OutputStream os, final int requestType, 
            SearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        final TabAlgorithm algorithmTab = searchRequest.getAlgorithm();

        final String queryHash = searchRequest.getInputAndSizeHashCode();
        final Broadcaster bcaster;

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

        try {
            final Iterator docIterator = bcaster.docIterator();
            if (requestType == DOCUMENT_REQUEST) {
                //
                // document request
                //
                final RawDocumentsSerializer serializer = serializerFactory.createRawDocumentSerializer(request);
                response.setContentType(serializer.getContentType());
                serializer.startResult(os);
                while (docIterator.hasNext()) {
                    serializer.write((RawDocument) docIterator.next());
                }
                serializer.endResult();
            } else {
                //
                // clustering request.
                //
                final HashMap props = new HashMap();
                props.put(RawDocumentsProducerLocalInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, 
                        docIterator);
                props.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, 
                        Integer.toString(searchRequest.getInputSize()));
                try {
                    logger.info("Clustering results using: " + algorithmTab.getShortName());
                    final ProcessingResult result = 
                        algorithmsController.query(algorithmTab.getShortName(), 
                                searchRequest.query, props);
                    final ClustersConsumerOutputComponent.Result collected =  
                        (ClustersConsumerOutputComponent.Result) result.getQueryResult();
                    final List clusters = collected.clusters; 
                    final List documents = bcaster.getDocuments();

                    final RawClustersSerializer serializer = serializerFactory.createRawClustersSerializer(request);
                    response.setContentType(serializer.getContentType());
                    serializer.startResult(os, documents);
                    for (Iterator i = clusters.iterator(); i.hasNext();) {
                        serializer.write((RawCluster) i.next());
                    }
                    serializer.endResult();

                    // TODO: add clustering result to the cache?
                } catch (Exception e) {
                    logger.warn("Error running input query.", e);
                }
            }
        } finally {
            synchronized (getServletContext()) {
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
     * Initializes results serializers.
     */
    private void initializeSerializers(ServletConfig conf) throws ServletException {
        String serializerFactoryClass = conf.getInitParameter("results.serializerFactory");
        if (serializerFactoryClass == null) {
            serializerFactoryClass = XMLSerializersFactory.class.getName();
            logger.warn("serializerFactory undefined, using the default: "
                    + serializerFactoryClass);
        }

        final SerializersFactory factory;
        try {
            factory = (SerializersFactory)
                Thread.currentThread().getContextClassLoader().loadClass(serializerFactoryClass).newInstance();
            factory.configure(conf);
        } catch (Exception e) {
            throw new ServletException("Could not create results serializer: "
                    + serializerFactoryClass, e);
        }
        logger.info("Using serializer factory: " + factory.getClass().getName());
        this.serializerFactory = factory;
    }

    /**
     * Initializes caches.
     */
    private void initializeCache(ServletConfig config) throws ServletException {
        final String EHCACHE_NAME = "carrot2";

        // Initialize EHCache
        final String cacheConfigResource = config.getInitParameter("ehcache.config.resource");
        if (cacheConfigResource == null) {
            throw new ServletException("EHCache configuration location (ehcache.config.resource) is missing.");
        }

        final URL configStream;
        try {
            configStream = super.getServletContext().getResource(cacheConfigResource);
        } catch (MalformedURLException e) {
            throw new ServletException("Resource URL malformed.", e);
        }
        if (configStream == null) {
            throw new ServletException("EHCache configuration is missing: " + cacheConfigResource);
        }
        
        final CacheManager cm = CacheManager.create(configStream);
        if (!cm.cacheExists(EHCACHE_NAME)) {
            throw new ServletException("EHCache for Carrot2 not defined ("
                    + EHCACHE_NAME + ")");
        }
        this.ehcache = cm.getCache(EHCACHE_NAME);
    }

    /**
     * Initializes and returns {@link LocalController} which will contain processes 
     * for collecting {@link RawDocument}s. This method also initializes {@link TabSearchInput}
     * instances corresponding to processes defined in the returned controller.
     */
    private LocalControllerBase initializeInputs(final File inputScripts, final SearchSettings settings) {
        if (!inputScripts.isDirectory()) {
            throw new RuntimeException("Scripts for input tabs not initialized.");
        }

        final LocalControllerBase controller = new LocalControllerBase();
        final ControllerHelper helper = new ControllerHelper();
        
        // Register context path for beanshell scripts.
        final ComponentFactoryLoader bshLoader = helper.getComponentFactoryLoader(ControllerHelper.EXT_COMPONENT_FACTORY_LOADER_BEANSHELL);
        if (bshLoader != null) {
            final HashMap globals = new HashMap();
            globals.put("inputsDirFile", inputScripts);
            ((BeanShellFactoryDescriptionLoader) bshLoader).setGlobals(globals);
        }

        // Add an output sink component now.
        controller.addLocalComponentFactory("collector",
                new LocalComponentFactory() {
                    public LocalComponent getInstance() {
                        return new BroadcasterPushOutputComponent();
                    }
                }
        );

        // Add document enumerator.
        controller.addLocalComponentFactory("enumerator",
                new LocalComponentFactory() {
                    public LocalComponent getInstance() {
                        return new RawDocumentEnumerator();
                    }
                }
        );

        // And now add inputs.
        try {
            final LoadedComponentFactory [] factories = 
                helper.loadComponentFactoriesFromDirectory(inputScripts);
            for (int i = 0; i < factories.length; i++) {
                final LoadedComponentFactory loaded = factories[i];
                // check if the required properties are present.
                final String tabName = (String) loaded.getProperty("tab.name");
                final String tabDesc = (String) loaded.getProperty("tab.description");
                if (tabName == null || tabDesc == null) {
                    logger.warn("The input factory: " + loaded.getId()
                            + " must specify 'tab.name' and 'tab.description' properties.");
                    continue;
                }
                controller.addLocalComponentFactory(loaded.getId(), loaded.getFactory());

                final Map otherProps = new HashMap();
                for (Iterator j = loaded.getProperties().entrySet().iterator(); j.hasNext();) {
                    final Map.Entry entry = (Map.Entry) j.next();
                    final String key = (String) entry.getKey();
                    if (key.startsWith("tab.")) {
                        otherProps.put(key, entry.getValue());
                    }
                }   

                final boolean ignoreOnError = "true".equals(loaded.getProperty("tab.ignoreOnError"));
                try {
                    final boolean defaultTab = "true".equals(loaded.getProperty("tab.default"));
                    controller.addProcess(tabName, new LocalProcessBase(loaded.getId(), "collector", 
                            /* filters */ new String[] { "enumerator" } ));
                    settings.add(new TabSearchInput(tabName, tabDesc, otherProps));
                    if (defaultTab) {
                        settings.setDefaultTabIndex(settings.getInputTabs().size() - 1);
                    }
                    logger.info("Added input tab: " + tabName + " (component: " + loaded.getId() + ")");
                } catch (Exception e) {
                    if (ignoreOnError) {
                        // ignore exception.
                        logger.warn("Skipping input tab: " + tabName + " (ignored exception: " + e.getMessage() + ")");
                    } else {
                        // rethrow.
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Problems initializing input components.", e);
        }

        // Check if there are any inputs at all.
        if (settings.inputTabs.size() == 0) {
            throw new RuntimeException("At least one input tab must be defined.");
        }
        
        return controller;
    }

    /**
     * Initializes and returns a {@link LocalController} containing clustering algorithms.
     */
    private LocalControllerBase initializeAlgorithms(File algorithmScripts, SearchSettings searchSettings) {
        if (!algorithmScripts.isDirectory()) {
            throw new RuntimeException("Scripts for algorithm tabs not initialized.");
        }

        final LocalControllerBase controller = new LocalControllerBase();
        final ControllerHelper helper = new ControllerHelper();

        // Add an input producer component now.
        controller.addLocalComponentFactory("input-demo-webapp",
                new LocalComponentFactory() {
                    public LocalComponent getInstance() {
                        return new RawDocumentsProducerLocalInputComponent();
                    }
                }
        );
        // Add an output collector.
        controller.addLocalComponentFactory("output-demo-webapp",
                new LocalComponentFactory() {
                    public LocalComponent getInstance() {
                        return new ClustersConsumerOutputComponent();
                    }
                }
        );
        
        final FileFilter processFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().startsWith("alg-");
            }
        };
        final FileFilter componentFilter = new FileFilter() {
            private final FileFilter subFilter = 
                helper.getComponentFilter();
            public boolean accept(File file) {
                return this.subFilter.accept(file) &&
                    !file.getName().startsWith("alg-");
            }
        };

        try {
            helper.addComponentFactoriesFromDirectory(
                    controller, algorithmScripts, componentFilter);
            final LoadedProcess [] processes = 
                helper.loadProcessesFromDirectory(algorithmScripts, processFilter);
            for (int i = 0; i < processes.length; i++) {
                final String shortName = processes[i].getProcess().getName();
                final String description = processes[i].getProcess().getDescription();
                final boolean defaultAlgorithm = "true".equals(processes[i].getAttributes().get("process.default"));
                searchSettings.add(new TabAlgorithm(shortName, description));
                if (defaultAlgorithm) {
                    searchSettings.setDefaultAlgorithmIndex(searchSettings.getAlgorithms().size() - 1);
                }
                controller.addProcess(shortName, processes[i].getProcess());
                logger.info("Added algorithm: " + shortName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Problems initializing algorithms.", e);
        }
        
        return controller;
    }
    
    /**
     * Attempts to send an internal server error HTTP error, if possible.
     * Otherwise simply pushes the exception message to the output stream. 

     * @param message Message to be printed to the logger and to the output stream.
     * @param t Exception that caused the error.
     */
    protected void servletError(HttpServletRequest origRequest, HttpServletResponse origResponse, OutputStream os, String message, Throwable t) {
        final Writer writer;
        try {
            writer = new OutputStreamWriter(os, Constants.ENCODING_UTF);
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(Constants.ENCODING_UTF + " must be supported.");
        }
        logger.warn(message, t);
        if (false == origResponse.isCommitted()) {
            // Reset the buffer and previous status code.
            origResponse.reset();
            origResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            origResponse.setContentType(Constants.MIME_HTML_CHARSET_UTF);
        }

        // Response committed. Just push the error to the output stream.
        try {
            writer.write("<h1 style=\"color: red; margin-top: 1em;\">");
            writer.write("Internal server exception");
            writer.write("</h1>");
            writer.write("<b>URI</b>: " + origRequest.getRequestURI() + "\n<br/><br/>");
            serializeException(writer, t);
            if (t instanceof ServletException && ((ServletException) t).getRootCause() != null) {
                writer.write("<br/><br/><h2>ServletException root cause:</h2>");
                serializeException(writer, ((ServletException) t).getRootCause());
            }
            writer.flush();
        } catch (IOException e) {
            // not much to do in such case (connection broken most likely).
            logger.warn("Exception info could not be returned to client (I/O).");
        }
    }

    /**
     * Utility method to serialize an exception and its stack trace to simple HTML.
     */
    private final void serializeException(Writer osw, Throwable t) throws IOException {
        osw.write("<b>Exception</b>: " + t.toString() + "\n<br/><br/>");
        osw.write("<b>Stack trace:</b>");
        osw.write("<pre style=\"margin: 1px solid red; padding: 3px; font-family: sans-serif; font-size: small;\">");
        PrintWriter pw = new PrintWriter(osw);
        t.printStackTrace(pw);
        pw.flush();
        osw.write("</pre>");
    }
}
