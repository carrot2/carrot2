

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.remote.controller.cache;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;


/**
 * Caches queries to input components.
 */
public class Cache
{
    private static final Logger log = Logger.getLogger(Cache.class);
    private CachedQueriesContainer [] containers;
    private List containersList = new ArrayList();

    public Cache()
    {
    }

    public void addContainer(CachedQueriesContainer c)
    {
        log.debug("Adding cache container: " + c.getClass().getName());
        containersList.add(c);
    }


    public void configure()
    {
        if (this.containersList == null)
        {
            throw new RuntimeException("Add containers before calling configure().");
        }

        // reverse the order of containers.
        Collections.reverse(containersList);

        this.containers = new CachedQueriesContainer[containersList.size()];
        containersList.toArray(containers);

        for (int i = 0; i < containers.length; i++)
        {
            containers[i].configure();

            if (i >= 1)
            {
                containers[i].addCacheListener(new CachedQueryRotator(i));
            }
        }

        log.info("Cache(s) initialized and configured (" + containers.length + ") in chain.");
    }

    /**
     * Reacts to an overfull state in cachedquery containers by moving the 'overful' elements from
     * one container to another.
     */
    public class CachedQueryRotator
        implements CacheListener
    {
        private final int associatedContainerPosition;

        public CachedQueryRotator(int associatedContainerPosition)
        {
            this.associatedContainerPosition = associatedContainerPosition;
        }

        public void elementRemovedFromCache(CachedQuery q)
        {
            log.debug("Degrading cached query to lower container...");

            // move the element down container list
            containers[associatedContainerPosition - 1].addToCache(q);
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
}
