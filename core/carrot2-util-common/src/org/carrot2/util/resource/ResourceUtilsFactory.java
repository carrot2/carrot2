/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import java.io.File;

/**
 * A factory of {@link ResourceUtils}.
 */
public final class ResourceUtilsFactory
{
    /** Default {@link ResourceUtils}. */
    public static final ResourceUtils DEFAULT = new ResourceUtils(
        getDefaultResourceLocators());

    /**
     * Return the default resource-lookup locators.
     */
    public static ResourceLocator [] getDefaultResourceLocators()
    {
        final ResourceLocator [] base = new ResourceLocator []
        {
            // Absolute resource files
            new AbsoluteFilePathLocator(),
            // Current working directory
            new DirLocator(new File(".").getAbsolutePath()),
            // Context class loader-relative
            new ContextClassLoaderLocator(),
            // Given class-relative resources
            new ClassRelativeLocator(),
        };

        return base;
    }

    /**
     * Return the default resource lookup proxy.
     */
    public static ResourceUtils getDefaultResourceUtils()
    {
        return DEFAULT;
    }
}
