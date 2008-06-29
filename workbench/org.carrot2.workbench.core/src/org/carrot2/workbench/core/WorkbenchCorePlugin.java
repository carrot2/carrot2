package org.carrot2.workbench.core;

import java.util.HashMap;

import org.carrot2.core.CachingController;
import org.carrot2.core.DocumentSource;
import org.carrot2.workbench.core.helpers.VelocityHelper;
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
        VelocityHelper.init();

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
}
