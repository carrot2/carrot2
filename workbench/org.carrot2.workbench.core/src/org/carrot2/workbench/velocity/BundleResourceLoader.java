
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * A Velocity {@link ResourceLoader} that loads resources relative to a given bundle.
 * A local cache is used to store resource URLs to increase performance.
 */
public final class BundleResourceLoader extends ResourceLoader
{
    /**
     * Set this Velocity property to the identifier of the bundle that should be used to
     * load templates.
     */
    public static final String BUNDLE_ID = "bundle.id";
    
    /**
     * Bundle relative to which resources are resolved.
     */
    private Bundle bundle;

    /**
     * Prefix prepended to all resources.
     */
    private String prefix;

    /*
     * 
     */
    public BundleResourceLoader(String pluginId, String prefix)
    {
        this.bundle = locateBundle(pluginId);
        this.prefix = prefix;
    }

    /**
     * 
     */
    @Override
    public InputStream getResourceStream(String s) throws ResourceNotFoundException
    {
        final URL resource = FileLocator.find(bundle, new Path(prefix + s), null);
        if (resource == null)
        {
            throw new ResourceNotFoundException("Not found: " + s);
        }
        
        try
        {
            return resource.openStream();
        }
        catch (IOException e)
        {
            throw new ResourceNotFoundException("Failed to open: " + s, e);
        } 
    }

    /**
     * 
     */
    @Override
    public void init(ExtendedProperties props)
    {
        if (bundle != null)
        {
            return;
        }

        final String bundleID = props.getString(BUNDLE_ID, null);
        if (bundleID == null)
        {
            throw new RuntimeException("Initialize bundle key in Velocity properties: "
                + BUNDLE_ID);
        }

        this.bundle = locateBundle(bundleID);
    }

    /**
     * 
     */
    @Override
    public boolean isSourceModified(Resource resource)
    {
        return false;
    }

    /**
     * 
     */
    @Override
    public long getLastModified(Resource resource)
    {
        return 0L;
    }

    /*
     * 
     */
    private Bundle locateBundle(String bundleID)
    {
        final Bundle bundle = Platform.getBundle(bundleID);
        if (bundle == null)
        {
            throw new RuntimeException("Bundle not found: " + bundleID);
        }
        return bundle;
    }
}
