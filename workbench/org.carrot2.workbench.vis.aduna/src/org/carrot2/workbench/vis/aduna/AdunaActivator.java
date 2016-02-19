
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
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        PLUGIN_ID = context.getBundle().getSymbolicName();
        plugin = this;
    }
}
