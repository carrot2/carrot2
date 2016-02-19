
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

import java.util.Arrays;
import java.util.HashMap;

import org.carrot2.util.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.collect.MapMaker;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A static cache of immutable, reusable resources associated with a given
 * {@link ResourceLookup}. Roughly the same thing can be achieved with a
 * {@link MapMaker}, but we want some logging in place too.
 * 
 * @param <V> Resource value class.
 */
@ThreadSafe
public final class ResourceCache<V>
{
    /**
     * Logger.
     */
    private static Logger logger = LoggerFactory.getLogger(ResourceCache.class);

    /**
     * Internal map for storing resources associated with a given {@link ResourceLookup}.
     */
    private final HashMap<ResourceLookup, V> cache = Maps.newHashMap();

    /**
     * Value maker.
     */
    private final Function<ResourceLookup, V> valueMaker;

    /**
     * Create a resource cache with the provided value maker.
     */
    public ResourceCache(Function<ResourceLookup, V> valueMaker)
    {
        this.valueMaker = valueMaker;
    }

    /**
     * Acquire a resource from the resource lookup.
     */
    public V get(ResourceLookup resourceLookup, boolean recreate)
    {
        // If no hit or refreshing takes place, reload everything under that key.
        synchronized (cache)
        {
            if (recreate || !cache.containsKey(resourceLookup))
            {
                logger.debug("{} resources, locations: {}", new Object [] {
                    recreate ? "Reloading" : "Loading",
                    Arrays.toString(resourceLookup.getLocators())});

                // Reload all resources for the current configuration. 
                cache.put(resourceLookup, valueMaker.apply(resourceLookup));
            }

            // Must return non-null.
            return cache.get(resourceLookup);
        }
    }
}
