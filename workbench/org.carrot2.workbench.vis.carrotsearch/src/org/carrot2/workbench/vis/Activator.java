
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
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
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

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

    /**
     * An URL to statically served resources (visualizations).
     */
    private String staticResourceURL;

    /*
     * 
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        final URL bundleURL = FileLocator.find(getBundle(), new Path("web"), null);
        URL resourceURL = FileLocator.toFileURL(bundleURL);

        LoggerFactory.getLogger(Activator.class).debug("Bundled resources at: "
            + resourceURL.toExternalForm());

        if (!"file".equals(resourceURL.getProtocol())) {
            throw new Exception("Expected file protocol on bundled Web resources: "
                + resourceURL.toExternalForm());
        }

        this.staticResourceURL = resourceURL.toExternalForm();
        while (this.staticResourceURL.endsWith("/")) {
            this.staticResourceURL = staticResourceURL.substring(0, staticResourceURL.length() - 1);
        }

        instance = this;
    }

    /*
     * 
     */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;
        super.stop(context);
    }

    /**
     * Returns a full URL to the internal built-in HTTP server, based on the
     * relative URI to a resource.
     */
    public String getFullURL(String relativeURL)
    {
        final String base = staticResourceURL;

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
            for (Map.Entry<SearchEditor, Integer> e : editors.entrySet())
            {
                if (e.getValue().intValue() == id)
                {
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
