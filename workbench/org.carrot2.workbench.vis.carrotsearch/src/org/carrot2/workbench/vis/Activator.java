
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

package org.carrot2.workbench.vis;

import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

import org.carrot2.workbench.core.ui.SearchEditor;
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

    /**
     * Editor ID assignments.
     */
    private WeakHashMap<SearchEditor, Integer> editors = new WeakHashMap<SearchEditor, Integer>();

    /**
     * ID sequencer.
     */
    private int sequencer;

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

    /**
     * Retrieve the editor associated with the given ID.
     */
    public SearchEditor getEditor(int id)
    {
        synchronized (this)
        {
            for (Map.Entry<SearchEditor, Integer> e : editors.entrySet()) {
                if (e.getValue().intValue() == id) {
                    return e.getKey();
                }
            }
        }

        return null;
    }

    /**
     * Register an editor or retrieve its unique ID for communicating with 
     * web pages.
     */
    public int registerEditor(SearchEditor editor)
    {
        synchronized (this)
        {
            Integer id = editors.get(editor);
            if (id != null)
                return id;

            editors.put(editor, ++sequencer);
            return sequencer;
        }
    }
}
