
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

package org.carrot2.util.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.carrot2.util.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Resource loading utility scanning one or more {@link IResourceLocator} locations. If
 * all locators provided to this class are thread-safe, this class is also thread-safe.
 */
@ThreadSafe
public final class ResourceLookup
{
    /** Logger for lookup events. */
    private final static Logger logger = LoggerFactory.getLogger(ResourceLookup.class);

    /**
     * An array of resource locators (used first to last).
     */
    private final IResourceLocator [] locators;

    /**
     * A set of predefined locations.
     */
    public static enum Location
    {
        /**
         * Resources reachable via the current thread's context class loader.
         */
        CONTEXT_CLASS_LOADER(new ContextClassLoaderLocator()),

        /**
         * Resources in the current working directory.
         */
        CURRENT_WORKING_DIRECTORY(new DirLocator(new File(".")));

        /**
         * The locator associated with the location.
         */
        public final IResourceLocator locator;

        /**
         * Constructor for enum constants.
         */
        private Location(IResourceLocator locator)
        {
            this.locator = locator;
        }
    }
    
    /**
     * Create a lookup object based on the provided array of predefined
     * locations. 
     */
    public ResourceLookup(Location... locations)
    {
        this.locators = new IResourceLocator [locations.length];
        for (int i = 0; i < locators.length; i++)
        {
            locators[i] = locations[i].locator;
        }
    }

    /**
     * Create a lookup object based on the provided array of resource locators (in order).
     */
    public ResourceLookup(IResourceLocator... locators)
    {
        this.locators = Arrays.copyOf(locators, locators.length); 
    }

    /**
     * Create a lookup object based on the provided list of resource locators (in order).
     */
    public ResourceLookup(List<IResourceLocator> locators)
    {
        this(locators.toArray(new IResourceLocator [locators.size()])); 
    }

    /**
     * Scans all resource locators and returns matching resources.
     * 
     * @param resource Resource name.
     * @return Returns an empty array if no resource matched the given name.
     */
    public IResource [] getAll(String resource)
    {
        final StringBuilder logEntry = new StringBuilder();
        logEntry.append("getAll(").append("):\n\t").append(resource);

        final ArrayList<IResource> result = Lists.newArrayList();
        for (final IResourceLocator locator : locators)
        {
            final IResource [] hits = locator.getAll(resource);

            logEntry.append("\n\t- ")
                    .append(hits.length + " " + pluralize("hit", hits.length) + " from: ")
                    .append(locator);

            for (int j = 0; j < hits.length; j++)
            {
                logEntry.append("\n\t  - ")
                        .append(hits[j]);

                if (!result.contains(hits[j]))
                {
                    result.add(hits[j]);
                }
            }
        }

        logger.debug(logEntry.toString());
        return result.toArray(new IResource [result.size()]);
    }

    /**
     * Scans through resource locators and returns the first matching resource.
     * 
     * @param resource Resource name.
     * @return Returns null if no resource was found for the given name.
     */
    public IResource getFirst(String resource)
    {
        final StringBuilder logEntry = new StringBuilder();
        logEntry.append("getFirst(").append("):\n\t").append(resource);

        IResource result = null;
        for (final IResourceLocator locator : locators)
        {
            if (result == null)
            {
                final IResource [] hits = locator.getAll(resource);
                
                final int hitsLength = hits != null ? hits.length : 0;
                logEntry.append("\n\t- ")
                        .append(hitsLength + " " + pluralize("hit", hitsLength) + " from: ")
                        .append(locator);

                if (hits != null && hitsLength > 0)
                {
                    for (int i = 0; i < hitsLength; i++)
                    {
                        logEntry.append("\n\t\t- ")
                                .append(hits[0]);
                    }
                    result = hits[0];
                }
            }
            else
            {
                logEntry.append("\n\t- 0 hits [not scanned] from: ")
                        .append(locator);
            }
        }

        logger.debug(logEntry.toString());
        return result;
    }

    /**
     * Returns a copy of the internal locators array.
     */
    public IResourceLocator [] getLocators()
    {
        return Arrays.copyOf(this.locators, this.locators.length);
    }

    /**
     * Pluralize a string.
     */
    private static String pluralize(String string, int value)
    {
        if (value == 1) 
            return string;
        else
            return string + "s";
    }
    
    @Override
    public int hashCode()
    {
        return ArrayUtils.hashCode(this.locators);
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof ResourceLookup)
        {
            return ArrayUtils.isEquals(
                this.locators, ((ResourceLookup) target).locators);
        }

        return false;
    }
    
    @Override
    public String toString() {
      return "[" + this.getClass().getSimpleName() + ": " + Arrays.toString(locators) + "]";
    }
}
