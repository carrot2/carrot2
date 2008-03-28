package org.carrot2.workbench.core;

import org.carrot2.workbench.core.helpers.CachingHelper;
import org.carrot2.workbench.core.helpers.VelocityHelper;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class CorePlugin extends AbstractUIPlugin
{
    // The plug-in ID
    public static final String PLUGIN_ID = "org.carrot2.workbench.core";

    // The shared instance
    private static CorePlugin plugin;

    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
        VelocityHelper.init();
    }

    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        CachingHelper.disposeAllControllers();
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static CorePlugin getDefault()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

}
