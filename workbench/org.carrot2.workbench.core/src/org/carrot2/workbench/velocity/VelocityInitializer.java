package org.carrot2.workbench.velocity;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.log.NullLogChute;

/**
 * See {@link #createInstance()}.
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
}
