

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2;


import com.dawidweiss.carrot.controller.carrot2.beanshell.BeanShellBSFEngine;
import com.dawidweiss.carrot.controller.carrot2.components.*;
import com.dawidweiss.carrot.controller.carrot2.guard.*;
import com.dawidweiss.carrot.controller.carrot2.process.*;
import com.dawidweiss.carrot.controller.carrot2.process.cache.*;
import com.dawidweiss.carrot.controller.carrot2.process.cache.Cache;
import com.dawidweiss.carrot.controller.carrot2.process.cache.file.*;
import com.dawidweiss.carrot.util.Log4jStarter;
import org.apache.log4j.*;
import org.exolab.castor.xml.Unmarshaller;
import org.jdom.*;
import org.jdom.input.*;
import org.put.util.component.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;


/**
 * Application initialization. This servlet reads all configuration files and prepares environment
 * for Struts actions by placing references to processor and other components in application
 * context's attributes.
 */
public class Carrot2InitServlet
    extends HttpServlet
{
    /** Logger instance */
    private static final Logger log = Logger.getLogger(Carrot2InitServlet.class);

    /**
     * If this argument is present in application context, the initialization failed for some
     * reason. The value held by this argument should be a Throwable denoting the cause of
     * initialization failure.
     */
    public static final String CARROT_INIT_ERROR_KEY = "CARROT_INIT_ERROR";
    public static final String CARROT_PROCESSOR_KEY = "CARROT_PROCESSOR";
    public static final String CARROT_PROCESSINGCHAINS_LOADER = "CARROT_PROCESSINGCHAINS_LOADER";

    /** Main configuration file. */
    private File config;

    /** Allows dynamic configuration reloading. Disable by default. */
    private boolean allowReloading = false;

    /**
     * Initialize Carrot2 environment.
     *
     * @param servletConfig Servlet configuration passed from servlet container.
     */
    public void init(ServletConfig servletConfig)
        throws ServletException
    {
        super.init(servletConfig);

        // Install beanshell support.
        org.apache.bsf.BSFManager.registerScriptingEngine(
            "beanshell", BeanShellBSFEngine.class.getName(), new String [] { "bsh" }
        );

        String configFileName = servletConfig.getInitParameter("config");
        initialize(
            servletConfig.getServletContext(),
            new File(servletConfig.getServletContext().getRealPath(configFileName))
        );
    }


    /**
     * GET method may be used to reconfigure Carrot2 processing environment. It basically flushes
     * all context references and re-reads configuration files. This option must be allowed in the
     * primary configuration XML.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        if (allowReloading)
        {
            // reload configuration file and recreate environment.
            try
            {
                log.info("Configuration reload requested [" + new Date() + "]");
                initialize(getServletContext(), config);
                getServletContext().getRequestDispatcher("/").forward(request, response);
            }
            catch (Exception e)
            {
                log.error("Error when reloading configuration.", e);
                request.setAttribute("exception", e);

                try
                {
                    getServletContext().getRequestDispatcher("/error.jsp").forward(
                        request, response
                    );
                }
                catch (IOException x)
                {
                    log.fatal("Cannot redirect to error.jsp", x);
                    throw new ServletException("No errors page.", x);
                }
            }
        }
        else
        {
            // not allowed. Send HTTP error.
            try
            {
                response.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Carrot2 configuration set to non-reloading mode."
                );
            }
            catch (IOException e)
            {
                // simply ignore requests when reloading disabled.
                log.warn("GET request when reloading disabled from " + request.getRemoteHost());
            }
        }
    }


    /* -------------------------------------------------------------------- protected methods */

    /**
     * Loads the XML configuration file and initializes environment.
     */
    private void initialize(ServletContext context, File configurationFile)
    {
        try
        {
            if ((configurationFile == null) || !configurationFile.canRead())
            {
                // cannot read configuration file. Set up context setup error.
                throw new RuntimeException("errors.initialization.missing-config");
            }
            else
            {
                config = configurationFile;

                // load the XML configuration resource.
                SAXBuilder configBuilder = new SAXBuilder(false);
                Element root = configBuilder.build(configurationFile).getRootElement();

                if (
                    (root.getAttribute("reloadable") != null)
                        && "true".equalsIgnoreCase(root.getAttribute("reloadable").getValue())
                )
                {
                    this.allowReloading = true;
                }
                else
                {
                    this.allowReloading = false;
                }

                // initialize log4j.
                Log4jStarter.getLog4jStarter().initializeLog4j(getServletConfig());

                // load the default set of components
                ComponentsLoader componentLoader = new ComponentsLoader();
                List defaultComponents = root.getChildren("components");

                for (Iterator i = defaultComponents.iterator(); i.hasNext();)
                {
                    Element e = (Element) i.next();
                    componentLoader.addComponentsFromDirectory(
                        new File(context.getRealPath(e.getAttribute("contextDir").getValue()))
                    );
                }

                // load the default set of processing chains and associate them
                // with components loaded.
                ProcessingChainLoader processLoader = new ProcessingChainLoader(componentLoader);
                List defaultProcesses = root.getChildren("processes");

                for (Iterator i = defaultProcesses.iterator(); i.hasNext();)
                {
                    Element e = (Element) i.next();
                    processLoader.addProcessesFromDirectory(
                        new File(context.getRealPath(e.getAttribute("contextDir").getValue()))
                    );
                }

                //
                // Instantiate caches and QueryProcessor
                //
                Cache cache;

                Element cacheConfig = root.getChild("cache");

                if ((cacheConfig != null) && (cacheConfig.getAttribute("config") != null))
                {
                    File cacheConfigFile = new File(
                            context.getRealPath(cacheConfig.getAttribute("config").getValue())
                        );

                    if (!cacheConfigFile.exists() || !cacheConfigFile.canRead())
                    {
                        log.error(
                            "Cache configuration file cannot be read: "
                            + cacheConfigFile.getAbsolutePath()
                        );
                        throw new RuntimeException("errors.initialization.cache-config-missing");
                    }

                    // parse the cache specification XML.
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    factory.setValidating(false);

                    DocumentBuilder builder = factory.newDocumentBuilder();
                    org.w3c.dom.Document document = builder.parse(cacheConfigFile);

                    org.w3c.dom.NodeList nlist = document.getElementsByTagName("container");

                    cache = new Cache();

                    for (int i = 0; i < nlist.getLength(); i++)
                    {
                        org.w3c.dom.Node container = nlist.item(i);
                        String implementation = container.getAttributes()
                                                         .getNamedItem("implementation")
                                                         .getNodeValue();

                        Class clazz = this.getClass().getClassLoader().loadClass(implementation);
                        CachedQueriesContainer cqc = (CachedQueriesContainer) Unmarshaller
                            .unmarshal(clazz, container);
                        cache.addContainer(cqc);

                        if (cqc instanceof AbstractFilesystemCachedQueriesContainer)
                        {
                            ((AbstractFilesystemCachedQueriesContainer) cqc).setServletBase(
                                this.getServletContext().getRealPath("/")
                            );
                        }
                    }
                }
                else
                {
                    cache = new Cache();
                }

                cache.configure();

                //
                // Instantiate QueryGuard(s)
                //
                QueryGuard guard = null;

                Element guardDirElement = root.getChild("guards");

                if (
                    (guardDirElement != null)
                        && (guardDirElement.getAttribute("contextDir") != null)
                )
                {
                    File guardsDir = new File(
                            context.getRealPath(
                                guardDirElement.getAttribute("contextDir").getValue()
                            )
                        );

                    if (!guardsDir.isDirectory())
                    {
                        log.error(
                            "Guards directory cannot be read: " + guardsDir.getAbsolutePath()
                        );
                        throw new RuntimeException("errors.initialization.guards-dir-unavailable");
                    }

                    File [] guards = guardsDir.listFiles(
                            new FileFilter()
                            {
                                public boolean accept(File f)
                                {
                                    return f.isFile() && f.getName().endsWith(".xml");
                                }
                            }
                        );

                    if (guards.length > 0)
                    {
                        Loader loader = Loader.loadLoader(
                                QueryGuard.class.getResourceAsStream("loader-config.xml")
                            );

                        guard = new QueryGuardsSet();

                        for (int i = 0; i < guards.length; i++)
                        {
                            log.debug("Adding query guard: " + guards[i].getName());

                            try
                            {
                                QueryGuard g = (QueryGuard) loader.load(
                                        new FileInputStream(guards[i])
                                    );
                                ((QueryGuardsSet) guard).addGuard(g);
                            }
                            catch (Exception e)
                            {
                                log.error(
                                    "Cannot load guard definition from file: "
                                    + guards[i].getAbsolutePath(), e
                                );
                                throw new RuntimeException(
                                    "Cannot load guard definition from file: "
                                    + guards[i].getAbsolutePath()
                                );
                            }
                        }
                    }
                }

                // Instantiate QueryProcessor
                QueryProcessor processor = new QueryProcessor(cache, guard);
                context.setAttribute(Carrot2InitServlet.CARROT_PROCESSOR_KEY, processor);
                processor.setFullDebugInfo(
                    Boolean.valueOf(root.getAttributeValue("fullDebugReport", "false"))
                           .booleanValue()
                );

                //
                // Add processing chains to the application context.
                //
                context.setAttribute(
                    Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER, processLoader
                );
            }

            context.removeAttribute(CARROT_INIT_ERROR_KEY);
        }
        catch (Throwable e)
        {
            log.fatal("Exception raised when initializing application.", e);
            context.setAttribute(CARROT_INIT_ERROR_KEY, e);
        }
    }
}
