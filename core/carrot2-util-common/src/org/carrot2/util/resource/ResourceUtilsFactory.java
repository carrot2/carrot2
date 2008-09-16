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
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.collect.Lists;

/**
 * A factory of {@link ResourceUtils}.
 */
public final class ResourceUtilsFactory
{
    /** Default {@link ResourceUtils}. */
    public static volatile ResourceUtils defaultResourceUtils;

    /** Default list of {@link ResourceLocator}s. */
    public static final ArrayList<ResourceLocator> defaultResourceLocators = Lists
        .newArrayList();

    /*
     * Initialize default resource locators.
     */
    static
    {
        addLast( 
            // Absolute resource files
            new AbsoluteFilePathLocator(),
            // Current working directory
            new DirLocator(new File(".").getAbsolutePath()),
            // Context class loader-relative
            new ContextClassLoaderLocator(),
            // Given class-relative resources
            new ClassRelativeLocator());
    }

    /**
     * Return the default resource-lookup locators.
     */
    public static ResourceLocator [] getDefaultResourceLocators()
    {
        return defaultResourceLocators
            .toArray(new ResourceLocator [defaultResourceLocators.size()]);
    }

    /**
     * Inserts a set of {@link ResourceLocator}s before existing defaults. Re-creates
     * {@link ResourceUtils} factory returned from {@link #getDefaultResourceUtils()}.
     */
    public static void addFirst(ResourceLocator... locators)
    {
        add(0, locators);
    }

    /**
     * Appends a set of {@link ResourceLocator}s after existing defaults. Re-creates
     * {@link ResourceUtils} factory returned from {@link #getDefaultResourceUtils()}.
     */
    public static void addLast(ResourceLocator... locators)
    {
        add(defaultResourceLocators.size(), locators);
    }

    /**
     * Adds a set of new {@link ResourceLocator} to the set of default resource locators,
     * at the given index. Re-creates {@link ResourceUtils} factory returned from
     * {@link #getDefaultResourceUtils()}.
     */
    private static void add(int index, ResourceLocator... locators)
    {
        synchronized (ResourceUtilsFactory.class)
        {
            defaultResourceLocators.addAll(index, Arrays.asList(locators));
            defaultResourceUtils = new ResourceUtils(defaultResourceLocators
                .toArray(new ResourceLocator [defaultResourceLocators.size()]));
        }
    }

    /**
     * Return the default resource lookup proxy.
     */
    public static ResourceUtils getDefaultResourceUtils()
    {
        return defaultResourceUtils;
    }
}
