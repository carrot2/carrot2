
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.kohsuke.args4j.*;
import org.mortbay.component.LifeCycle;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;

/**
 * Bootstraps the Document Clustering Server.
 */
public class DcsApp
{
    private final Logger log = org.slf4j.LoggerFactory.getLogger("dcs");

    @Option(name = "-port", usage = "Port number to bind to")
    int port = 8080;

    @Option(name = "-v", aliases =
    {
        "--verbose"
    }, required = false, usage = "Print detailed messages")
    boolean verbose;

    @Option(name = "--accept-queue", required = false, 
        usage = "Socket accept queue length (default 20).")
    int acceptQueue = 20;

    @Option(name = "--threads", required = false, 
        usage = "Maximum number of processing threads (default 4).")
    int maxThreads = 4;

    String appName;
    Server server;

    /** 
     * Empty implementation of {@link LifeCycle.Listener}.
     */
    private static class ListenerAdapter implements LifeCycle.Listener
    {
        public void lifeCycleFailure(LifeCycle lc, Throwable t) { }
        public void lifeCycleStarted(LifeCycle lc) { }
        public void lifeCycleStarting(LifeCycle lc) { }
        public void lifeCycleStopped(LifeCycle lc) { }
        public void lifeCycleStopping(LifeCycle lc) { }
    }    
    
    DcsApp(String appName)
    {
        this.appName = appName;
    }

    void start() throws Exception
    {
        start(null);
    }

    void start(String webPathPrefix) throws Exception
    {
        System.setProperty("org.mortbay.log.class", Log4jJettyLog.class.getName());

        log.info("Starting DCS...");

        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setReuseAddress(false);
        connector.setAcceptQueueSize(acceptQueue);
        server.addConnector(connector);

        // http://issues.carrot2.org/browse/CARROT-581
        if (maxThreads < 2)
        {
            throw new IllegalArgumentException("Max number of threads must be greater than 1.");
        }
            
        final QueuedThreadPool tp = new QueuedThreadPool(maxThreads);
        server.setThreadPool(tp);

        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/");
        wac.addLifeCycleListener(new ListenerAdapter()
        {
            public void lifeCycleStarted(LifeCycle lc)
            {
                log.info("DCS started on port: " + port);
            }
            
            
            public void lifeCycleFailure(LifeCycle lc, Throwable t)
            {
                log.error("DCS startup failure.");
                stop();
            }

            public void lifeCycleStopped(LifeCycle lc)
            {
                log.info("DCS stopped.");
            }
        });
        wac.setParentLoaderPriority(true);

        final String dcsWar = System.getProperty("dcs.war");
        if (dcsWar != null)
        {
            // WAR distribution provides, use it
            wac.setWar(dcsWar);
        }
        else
        {
            // Run from the provided web dir
            wac.setWar(webPathPrefix != null ? webPathPrefix + "/web" : "web");
            wac.setClassLoader(Thread.currentThread().getContextClassLoader());
        }

        if (System.getProperty("dcs.development.mode") != null)
        {
            wac.setDefaultsDescriptor("etc/webdefault.xml");
        }

        server.setHandler(wac);
        server.setStopAtShutdown(true);

        // Start the http server.
        try
        {
            server.start();
        }
        catch (Exception e)
        {
            stop();
            throw e;
        }
    }

    void stop()
    {
        if (server != null)
        {
            try
            {
                server.stop();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String [] args) throws Exception
    {
        final DcsApp dcs = new DcsApp("dcs");

        final CmdLineParser parser = new CmdLineParser(dcs);
        parser.setUsageWidth(80);

        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            System.out.print("Usage: " + dcs.appName);
            parser.printSingleLineUsage(System.out);
            System.out.println();
            parser.printUsage(System.out);

            System.out.println("\n" + e.getMessage());
            return;
        }

        try
        {
            dcs.start();
        }
        catch (Exception e)
        {
            dcs.log.error("Startup failure: " + ExceptionUtils.getMessage(e));
        }
    }
}
