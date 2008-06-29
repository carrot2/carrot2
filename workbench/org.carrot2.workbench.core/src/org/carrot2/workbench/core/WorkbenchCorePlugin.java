package org.carrot2.workbench.core;

import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.carrot2.core.CachingController;
import org.carrot2.core.Controller;
import org.carrot2.core.DocumentSource;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class (plug-in's entry point), controls the life-cycle and contains a
 * reference to the Carrot2 {@link Controller}.
 */
public class WorkbenchCorePlugin extends AbstractUIPlugin
{
    /** Plug-in ID. */
    public static final String PLUGIN_ID = "org.carrot2.workbench.core";

    /** The shared instance. */
    private static WorkbenchCorePlugin plugin;

    /**
     * Shared, thread-safe caching controller instance.
     */
    private CachingController controller;

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;

        initVelocityEngine();

        controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap<String, Object>());
    }

    /*
     * 
     */
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;

        controller.dispose();
        controller = null;

        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static WorkbenchCorePlugin getDefault()
    {
        return plugin;
    }

    /**
     * Returns an initialized shared controller instance.
     */
    public CachingController getController()
    {
        return controller;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Initialize Velocity engine.
     */
    private static void initVelocityEngine()
    {
        final Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", ClasspathResourceLoader.class
            .getName());

        // Disable separate Velocity logging.
        p.setProperty(RuntimeConstants.RUNTIME_LOG, "");

        try
        {
            Velocity.init(p);
        }
        catch (Exception e)
        {
            final IStatus status = new OperationStatus(IStatus.ERROR,
                WorkbenchCorePlugin.PLUGIN_ID, -2,
                "Error while initiating Velocity engine", e);
            WorkbenchCorePlugin.getDefault().getLog().log(status);
            Utils.showError(status);
        }
    }
}
