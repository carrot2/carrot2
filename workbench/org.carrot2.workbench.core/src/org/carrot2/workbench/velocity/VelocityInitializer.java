
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

package org.carrot2.workbench.velocity;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.tools.generic.EscapeTool;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * See {@link #createInstance(String, String)}.
 */
public final class VelocityInitializer
{
    private VelocityInitializer()
    {
        // No instances.
    }
    
    /**
     * Initialize Velocity engine instance, disables logging,
     * sets bundle-relative resource loader.
     */
    public static RuntimeInstance createInstance(String bundleID, String templatePrefix)
    {
        try
        {
            final ExtendedProperties p = new ExtendedProperties();
            p.setProperty("resource.loader", "bundle");
            p.setProperty("bundle.resource.loader.instance", new BundleResourceLoader(
                bundleID, templatePrefix));
            p.setProperty(RuntimeConstants.SET_NULL_ALLOWED, "true");

            // Disable separate Velocity logging.
            p.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, 
                NullLogChute.class.getName());
    
            final RuntimeInstance velocity = new RuntimeInstance();
            velocity.setConfiguration(p);
            return velocity;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Velocity initialization failed.", e);
        }
    }

    /**
     * Create Velocity context and place default tools into it.
     */
    public static VelocityContext createContext()
    {
        final VelocityContext context = new VelocityContext();
        
        context.put("esc", new EscapeTool());
        context.put("stringutils", new StringUtils());
        context.put("annotationutils", new AnnotationUtils());

        final IPreferenceStore store = WorkbenchCorePlugin.getDefault().getPreferenceStore();
        int maxFieldLength = store.getInt(PreferenceConstants.MAX_FIELD_LENGTH);
        if (maxFieldLength == 0) maxFieldLength = Integer.MAX_VALUE; 
        context.put("maxFieldLength", maxFieldLength);

        return context;
    }
}
