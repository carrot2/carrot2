
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

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.osgi.framework.*;

/**
 * Manages built-in Jetty Web service. Most of the code copied or inspired by Eclipse help
 * system (so blame them).
 */
final class WebServiceManager
{
    /** */
    private static final int AUTO_SELECT_JETTY_PORT = 0;

    /** */
    private int port = AUTO_SELECT_JETTY_PORT;

    /** */
    private String host;
    
    /** */
    private String webappName;

    /**
     * <code>other.info</code> field for {@link JettyConfigurator}.
     */
    private final String otherInfo;

    /**
     * 
     */
    private final String PREFERENCE_HOST;

    /**
     * 
     */
    private final String PREFERENCE_PORT;

    /**
     * Creates a HTTP service with <code>other.info</code> set to the given identifier.
     */
    public WebServiceManager(String otherInfo, String preferencePrefix)
    {
        this.otherInfo = otherInfo;
     
        if (StringUtils.isEmpty(preferencePrefix))
        {
            preferencePrefix = "";
        }

        PREFERENCE_HOST = preferencePrefix + "http.host";
        PREFERENCE_PORT = preferencePrefix + "http.port";
    }

    /*
     * 
     */
    public void start(String webappName, BundleContext context) throws Exception
    {
        final Dictionary<String, Object> d = new Hashtable<String, Object>();

        configureSettings(context);

        d.put("http.port", port);
        d.put("context.path", "/");
        d.put("other.info", otherInfo);

        JettyConfigurator.startServer(webappName, d);
        checkBundle();
        
        this.webappName = webappName;
    }

    /**
     * Ensures that the bundle with the specified name and the highest available version
     * is started and reads the port number
     */
    private void checkBundle() throws InvalidSyntaxException, BundleException
    {
        final Bundle bundle = Platform.getBundle("org.eclipse.equinox.http.registry");
        if (bundle != null && bundle.getState() == Bundle.RESOLVED)
        {
            bundle.start(Bundle.START_TRANSIENT);
        }

        // Save the port number Jetty selected for us.
        ServiceReference [] reference = bundle.getBundleContext()
            .getServiceReferences(
                "org.osgi.service.http.HttpService",
                "(other.info=" + otherInfo + ")");

        Object assignedPort = reference[0].getProperty("http.port");
        port = Integer.parseInt((String) assignedPort);
    }

    /**
     * 
     */
    public void stop() throws CoreException
    {
        try
        {
            JettyConfigurator.stopServer(webappName);
        }
        catch (Exception e)
        {
            Utils.logError("Failed to stop the Web service.", e, false);
        }
    }

    /**
     * 
     */
    public int getPort()
    {
        return port;
    }

    /**
     * 
     */
    public String getHost()
    {
        return host;
    }

    /**
     * Configure connection settings from plugin preferences.
     */
    public void configureSettings(BundleContext context)
    {
        try
        {
            int portOverride = Integer.parseInt(context.getProperty(PREFERENCE_PORT));
            if (portOverride == 0)
            {
                portOverride = AUTO_SELECT_JETTY_PORT;
            }
            this.port = portOverride;
        } 
        catch (NumberFormatException e)
        {
            this.port = AUTO_SELECT_JETTY_PORT;
        }

        String hostName = context.getProperty(PREFERENCE_HOST);
        if (StringUtils.isEmpty(hostName))
        {
            hostName = "127.0.0.1";
        }
        this.host = hostName;
    }
}
