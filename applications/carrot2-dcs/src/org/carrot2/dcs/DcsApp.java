package org.carrot2.dcs;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.util.xml.TemplatesPool;
import org.kohsuke.args4j.*;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Bootstraps the Document Clustering Server.
 */
public class DcsApp
{
    private final Logger log = Logger.getLogger("dcs");

    @Option(name = "-port", usage = "Port number to bind to")
    int port = 8080;

    @Option(name = "-v", aliases =
    {
        "--verbose"
    }, required = false, usage = "Print detailed messages")
    boolean verbose;

    String appName;

    Server server;

    DcsApp(String appName)
    {
        this.appName = appName;
    }

    void go() throws Exception
    {
        start();
    }

    void start() throws Exception
    {
        start(null);
    }

    void start(String webPathPrefix) throws Exception
    {
        System.setProperty("org.mortbay.log.class", Log4jJettyLog.class.getName());
        
        // Silence memory leak messages from TemplatesPool in command line mode
        // We use a hardcoded class name to avoid a dependency on Carrot2 core in DCS starter.
        Logger.getLogger("org.carrot2.util.xml.TemplatesPool").setLevel(Level.ERROR);
        
        log.info("Starting DCS...");
        
        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setAcceptQueueSize(20);
        server.addConnector(connector);

        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/");

        final String dcsWar = System.getProperty("dcs.war");
        if (dcsWar != null)
        {
            // WAR distribution provide, use it
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

        // Start the http server
        server.start();
        log.info("DCS started, point browser to: http://localhost:" + port + "/");
    }

    void stop() throws Exception
    {
        server.stop();
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

        dcs.go();
    }
}
