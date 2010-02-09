
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

package org.carrot2.workbench.vis.circles;

import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * Bundle activator class.
 */
public final class CirclesActivator extends AbstractUIPlugin
{
    /**
     * Plug identifier.
     */
    public final static String ID = "org.carrot2.workbench.vis.circles";
    
    /**
     * HTTP service name.
     */
    public final static String HTTP_SERVICE_NAME = ID + ".http-service";

    /**
     * Startup page.
     */
    private static final String STARTUP_RELATIVE_URL = "/index.vm";

    /** The shared instance. */
    private static CirclesActivator instance;

    /**
     * Built-in HTTP service.
     */
    private WebServiceManager webService;

    /*
     * 
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
        
        this.webService = new WebServiceManager(ID, "circles.");
        this.webService.start(HTTP_SERVICE_NAME, context);

        final URL bundleURL = FileLocator.find(getBundle(), new Path("web"), null);
        final URL fileURL = FileLocator.toFileURL(bundleURL);

        logInfo("Web service started: http://" + webService.getHost() + ":" + webService.getPort()
            + ", static resources at: "
            + fileURL.toExternalForm());
    }

    /*
     * 
     */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        this.webService.stop();
        this.webService = null;

        instance = null;
        super.stop(context);
    }

    /**
     * @return Returns the startup URL to the built-in HTTP server with 
     * visualization code.
     */
    public String getStartupURL()
    {
        return getFullURL(STARTUP_RELATIVE_URL);
    }

    /**
     * Returns a full URL to the internal built-in HTTP server, based on the
     * relative URI to a resource.
     */
    String getFullURL(String relativeURL)
    {
        final String base = "http://" + webService.getHost() + ":" + webService.getPort();
        
        if (!relativeURL.startsWith("/"))
        {
            return base + "/" + relativeURL;
        }
        else
        {
            return base + relativeURL;
        }
    }

    /**
     * Return the shared plugin instance.
     */
    public static CirclesActivator getInstance()
    {
        return instance;
    }

    
    /*
     * 
     */
    final void logInfo(String message)
    {
        getLog().log(new Status(Status.INFO, ID, message));
    }
}
