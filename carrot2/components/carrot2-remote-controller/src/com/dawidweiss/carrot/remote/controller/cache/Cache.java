
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.remote.controller.cache;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.Query;


/**
 * A cache of results of queries sent to input components. The entire output
 * stream is cached.
 * 
 * @author Dawid Weiss
 */
public class Cache
{
    private static final Logger log = Logger.getLogger(Cache.class);
    private final CachedQueriesContainer [] containers;

    public Cache() {
        this(Collections.EMPTY_LIST);
    }

    public Cache(List containers) {
        this.containers = configure(containers);
    }

    private static CachedQueriesContainer [] configure(List containersList)
    {
        // reverse the order of containers.
        final ArrayList copy = new ArrayList(containersList);
        Collections.reverse(copy);
        final CachedQueriesContainer [] containers = new CachedQueriesContainer[copy.size()];
        copy.toArray(containers);

        for (int i = 0; i < containers.length; i++)
        {
            containers[i].configure();
            log.debug("Cache container configured: " + containers[i]);

            if (i >= 1)
            {
                containers[i].addCacheListener(new CachedQueryRotator(containers[i-1]));
            }
        }

        log.info("Cache(s) initialized and configured (" + containers.length + ") in chain.");

        return containers;
    }

    /**
     * Reacts to an overfull state in cachedquery containers by moving the 'overful' elements from
     * one container to another.
     */
    public static class CachedQueryRotator
        implements CacheListener
    {
        private final CachedQueriesContainer nextContainer;

        public CachedQueryRotator(CachedQueriesContainer nextContainer)
        {
            this.nextContainer = nextContainer;
        }

        public void elementRemovedFromCache(CachedQuery q)
        {
            // move the element down container list
            log.debug("Degrading cached query to lower container...");
            nextContainer.addToCache(q);
        }
    }

    /**
     * Returns cached input (if present), or null for the given set of attributes (query,
     * componentId, optional parameters).
     */
    public InputStream getInputFor(Query q, String componentId, Map optionalParams)
    {
        // generate signature for such combination.
        Object signature = CachedQuery.generateSignature(q, componentId, optionalParams);

        log.debug("Searching for cached: " + signature);

        for (int i = containers.length - 1; i >= 0; i--)
        {
            CachedQuery cq;
            log.debug("container: " + containers[i]);

            if ((cq = containers[i].getCachedElement(signature)) != null)
            {
                log.debug("found.");

                try
                {
                    // promote the cached query to the first container.
                    if (i < (containers.length - 1))
                    {
                        log.debug("Promoting cached query (reuse)...");

                        CachedQuery cq2 = containers[i + 1].addToCache(cq);

                        try
                        {
                            if (cq2 == null)
                            {
                                log.warn("Could not promote query.");

                                return cq.getData();
                            }

                            containers[i].expungeFromCache(signature);
                            log.warn(
                                "promoted to " + containers[i + 1] + " and expunged from "
                                + containers[i]
                            );

                            return cq2.getData();
                        }
                        finally
                        {
                            log.debug("Promoted.");
                        }
                    }
                    else
                    {
                        log.debug("Topmost cache");

                        return cq.getData();
                    }
                }
                catch (IOException e)
                {
                    log.warn("IO exception when accessing cached object: " + e.toString());
                    containers[i].expungeFromCache(signature);
                }
            }
        }

        log.debug("not found.");

        return null;
    }


    /**
     * Initializes the caching process. The input stream is proxied and saved to a CachedQuery
     * object. When the entire input is read without any exceptions (EOF is reached), new
     * CachedQuery object is added to the objects in CachedQueriesContainer list.
     */
    public InputStream cacheInputFor(
        InputStream response, Query q, String componentId, Map optionalParams
    )
    {
        log.debug("Caching input...");

        QueryCacherProxy proxy = new QueryCacherProxy(
                this, response, q, componentId, optionalParams
            );

        return proxy.getInputStream();
    }


    /**
     * This is a callback method invoked by the CacheRedirector objects when the request has been
     * fully processed and can be cached.
     */
    void addToCache(CachedQuery q)
    {
        log.debug("Adding to cache...");

        Object signature = q.getSignature();

        for (int i = containers.length - 1; i >= 0; i--)
        {
            if (containers[i].getCachedElement(signature) != null)
            {
                return;
            }
        }

        // it is still possible that the object has already been added to
        // cache by other threads, but it is not a big problem - the two
        // copies will just coexist.
        containers[containers.length - 1].addToCache(q);
    }
    
    /**
     * Clears the content of read-write cache containers.
     */
    public void clear() {
        for (int i = containers.length - 1; i >= 0; i--) {
            containers[i].clear();
        }
    }
}
