package org.carrot2.workbench.core;

import java.net.URL;

import org.carrot2.util.resource.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * A {@Link ResourceLocator} searching for bundle resources with a given name. The bundle
 * is started if needed.
 */
final class BundleResourceLocator implements ResourceLocator
{
    private final static Resource [] EMPTY = new Resource [0];
    private final Bundle bundle;

    public BundleResourceLocator(Bundle bundle)
    {
        this.bundle = bundle;
    }

    public Resource [] getAll(String resource, Class<?> clazz)
    {
        if (bundle.getState() != Bundle.ACTIVE)
        {
            try
            {
                bundle.start();
            }
            catch (BundleException e)
            {
                return EMPTY;
            }
        }

        final URL result = bundle.getEntry(resource);
        if (result != null)
        {
            return new Resource []
            {
                new URLResource(result)
            };
        }

        return EMPTY;
    }
}
