package org.carrot2.workbench.vis;

import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Bundle activator.
 */
public class Activator extends AbstractUIPlugin
{
    /**
     * Bundle identifier.
     */
    public final static String ID = "org.carrot2.workbench.vis.carrotsearch";
    
    /**
     * HTTP service name.
     */
    public final static String HTTP_SERVICE_NAME = ID + ".http-service";

    /**
     * Built-in HTTP service.
     */
    private WebServiceManager webService;

    /**
     * 
     */
    private static Activator instance;

    /*
     * 
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        this.webService = new WebServiceManager(ID, "circles.");
        this.webService.start(HTTP_SERVICE_NAME, context);

        final URL bundleURL = FileLocator.find(getBundle(), new Path("web"), null);
        final URL fileURL = FileLocator.toFileURL(bundleURL);

        logInfo("Web service started: http://" + webService.getHost() + ":" + webService.getPort()
            + ", static resources at: "
            + fileURL.toExternalForm());
        
        instance = this;
    }

    /*
     * 
     */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;

        this.webService.stop();
        this.webService = null;

        super.stop(context);
    }

    /**
     * Returns a full URL to the internal built-in HTTP server, based on the
     * relative URI to a resource.
     */
    public String getFullURL(String relativeURL)
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

    /*
     * 
     */
    final void logInfo(String message)
    {
        getLog().log(new Status(Status.INFO, ID, message));
    }

    /*
     * 
     */
    public static Activator getInstance()
    {
        return instance;
    }
}
