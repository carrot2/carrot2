
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

import java.io.File;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

/**
 * Command-line start (embedded JETTY).
 * 
 * @author Dawid Weiss
 */
public final class Main
{
    /**
     * Starts embedded JETTY server.
     */
    private void startJetty(final int port) throws Exception
    {
        final Server server = new Server();
        server.setResolveRemoteHost(false);
        server.setStopGracefully(true);

        // Socket listener.
        final SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);

        final WebApplicationContext context = new WebApplicationContext(new File("web").getAbsolutePath());
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setContextPath("/");

        server.addContext(context);

        // Start the http server
        server.start();
    }

    /**
     * Command-line entry point.
     */
    public static void main(String [] args) throws Exception
    {
        new Main().startJetty(8080);
    }
}
