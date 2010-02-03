
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

    /** Default list of {@link IResourceLocator}s. */
    public static final ArrayList<IResourceLocator> defaultResourceLocators = Lists
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
    public static IResourceLocator [] getDefaultResourceLocators()
    {
        return defaultResourceLocators
            .toArray(new IResourceLocator [defaultResourceLocators.size()]);
    }

    /**
     * Inserts a set of {@link IResourceLocator}s before existing defaults. Re-creates
     * {@link ResourceUtils} factory returned from {@link #getDefaultResourceUtils()}.
     */
    public static void addFirst(IResourceLocator... locators)
    {
        add(0, locators);
    }

    /**
     * Appends a set of {@link IResourceLocator}s after existing defaults. Re-creates
     * {@link ResourceUtils} factory returned from {@link #getDefaultResourceUtils()}.
     */
    public static void addLast(IResourceLocator... locators)
    {
        add(defaultResourceLocators.size(), locators);
    }

    /**
     * Adds a set of new {@link IResourceLocator} to the set of default resource locators,
     * at the given index. Re-creates {@link ResourceUtils} factory returned from
     * {@link #getDefaultResourceUtils()}.
     */
    private static void add(int index, IResourceLocator... locators)
    {
        synchronized (ResourceUtilsFactory.class)
        {
            defaultResourceLocators.addAll(index, Arrays.asList(locators));
            defaultResourceUtils = new ResourceUtils(defaultResourceLocators
                .toArray(new IResourceLocator [defaultResourceLocators.size()]));
        }
    }

    /**
     * Removes a given {@link IResourceLocator} from the list of existing default locators.
     * Re-creates {@link ResourceUtils} factory returned from
     * {@link #getDefaultResourceUtils()}.
     */
    public static void remove(IResourceLocator... locators)
    {
        synchronized (ResourceUtilsFactory.class)
        {
            defaultResourceLocators.removeAll(Arrays.asList(locators));
            defaultResourceUtils = new ResourceUtils(defaultResourceLocators
                .toArray(new IResourceLocator [defaultResourceLocators.size()]));
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
