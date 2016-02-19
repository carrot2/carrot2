
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

package org.carrot2.workbench.core;

import java.net.URL;

import org.carrot2.util.resource.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * A {@Link ResourceLocator} searching for bundle resources with a given name. The bundle
 * is started if needed.
 */
final class BundleResourceLocator implements IResourceLocator
{
    private final Bundle bundle;

    public BundleResourceLocator(Bundle bundle)
    {
        this.bundle = bundle;
    }

    @Override
    public IResource [] getAll(String resource)
    {
        if (bundle.getState() != Bundle.ACTIVE)
        {
            try
            {
                bundle.start();
            }
            catch (BundleException e)
            {
                return new IResource [0];
            }
        }

        final URL result = bundle.getEntry(resource);
        if (result != null)
        {
            return new IResource []
            {
                new URLResource(result)
            };
        }

        return new IResource [0];
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof BundleResourceLocator)
        {
            BundleResourceLocator other = (BundleResourceLocator) target;
            return this.bundle.equals(other.bundle);
        }

        return false;
    }
    
    @Override
    public int hashCode()
    {
        return this.bundle.hashCode();
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " [bundle: "
            + bundle + "]";
    }
}
