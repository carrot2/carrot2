
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
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
    private final static IResource [] EMPTY = new IResource [0];
    private final Bundle bundle;

    public BundleResourceLocator(Bundle bundle)
    {
        this.bundle = bundle;
    }

    public IResource [] getAll(String resource, Class<?> clazz)
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
            return new IResource []
            {
                new URLResource(result)
            };
        }

        return EMPTY;
    }
}
