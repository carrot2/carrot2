package org.carrot2.webapp;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebApp
{
    /**
     * Starts embedded JETTY server.
     */
    private void startJetty(final int port) throws Exception
    {
        System.setProperty("org.mortbay.util.URI.charset", "utf-8");
        
        final Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setAcceptQueueSize(20);
        server.addConnector(connector);

        WebAppContext wac = new WebAppContext();
        wac.setClassLoader(Thread.currentThread().getContextClassLoader());
        wac.setContextPath("/");
        wac.setWar("web");
        wac.setDefaultsDescriptor("etc/webdefault.xml");
        server.setHandler(wac);
        server.setStopAtShutdown(true);

        server.start();
    }

    /**
     * Command-line entry point.
     */
    public static void main(String [] args) throws Exception
    {
        /*
         * This enables LOG4J logging in Jetty (specify on command-line, if possible).
         */
        System.setProperty("org.mortbay.log.LogFactory.noDiscovery", "false");
        
        new WebApp().startJetty(8080);
    }
}
