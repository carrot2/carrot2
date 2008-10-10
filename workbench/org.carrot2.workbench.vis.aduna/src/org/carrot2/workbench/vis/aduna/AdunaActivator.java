package org.carrot2.workbench.vis.aduna;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/*
 * 
 */
public class AdunaActivator extends AbstractUIPlugin
{
    public static AdunaActivator plugin;
    public static String PLUGIN_ID;

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        PLUGIN_ID = context.getBundle().getSymbolicName();
        plugin = this;
    }
}