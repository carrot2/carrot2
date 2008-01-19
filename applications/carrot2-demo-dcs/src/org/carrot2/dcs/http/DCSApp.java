
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

package org.carrot2.dcs.http;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.log4j.Level;
import org.carrot2.dcs.*;
import org.carrot2.util.StringUtils;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

/**
 * An in-process HTTP server exposing the clustering service (processing POSTed XMLs).
 * 
 * @author Dawid Weiss
 */
public class DCSApp extends AppBase
{
    private CliOptions opts;

    protected DCSApp()
    {
        super("dcs");
    }

    protected DCSApp(String heading)
    {
        super("dcs", heading);
    }
    
    /**
     * Command line entry point (main).
     */
    public static void main(String [] args) throws Exception
    {
        new DCSApp().go(args);
    }

    /**
     * Command line entry point (after parsing arguments).
     */
    protected void go(CommandLine options)
        throws ConfigurationException
    {
        // Toggle verbose mode.
        if (options.hasOption(opts.verbose.getOpt())) getLogger().setLevel(Level.DEBUG);

        // Initialize processes.
        final ControllerContext context = initializeContext(
            (File) CliOptions.getOption(options, opts.descriptorsDir, new File("descriptors")));

        // Parse command-line options (defaults).
        final HashMap processingOptions = new HashMap();
        processingOptions.put(
            ProcessingOptionNames.ATTR_PROCESSID, 
            opts.parseProcessIdOption(options, context));
        processingOptions.put(
            ProcessingOptionNames.ATTR_OUTPUT_FORMAT, 
            opts.parseOutputFormat(options));
        processingOptions.put(
            ProcessingOptionNames.ATTR_CLUSTERS_ONLY, 
            Boolean.toString(options.hasOption(opts.clustersOnly.getOpt())));

        getLogger().info("Starting standalone DCS server.");
        try
        {
            final int port = ((Number) options.getOptionObject(opts.port.getOpt())).intValue();
            final AppConfig appConfig = new AppConfig(context, getLogger(), processingOptions);
            startJetty(port, appConfig);
            getLogger().info("Accepting HTTP requests on port: " + port);
        }
        catch (Exception e)
        {
            getLogger().fatal("Could not start HTTP server: " + StringUtils.chainExceptionMessages(e));
            getLogger().debug("Could not start HTTP server (details)", e);
        }
    }

    /**
     * 
     */
    protected void initializeOptions(Options options)
    {
        opts = new CliOptions();

        CliOptions.addAll(options, new Object []
        {
            opts.descriptorsDir, opts.processName, opts.outputFormat, opts.clustersOnly,
            opts.verbose, 

            opts.port
        });
    }

    /**
     * Starts embedded JETTY server.
     */
    private void startJetty(final int port, final AppConfig config) throws Exception
    {
        final Server server = new Server();
        server.setResolveRemoteHost(false);
        server.setStopGracefully(true);

        // Socket listener.
        final SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);

        final WebApplicationContext context = new WebApplicationContext(new File(".").getAbsolutePath());
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setContextPath("/");

        server.addContext(context);

        // Pass controller context as a global application attribute.
        context.setAttribute(InitializationServlet.ATTR_APPCONFIG, config);

        // Start the http server
        server.start();
    }
}
