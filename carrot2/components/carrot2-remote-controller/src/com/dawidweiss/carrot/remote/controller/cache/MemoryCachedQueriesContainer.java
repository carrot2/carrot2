
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.remote.controller.cache;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.dawidweiss.carrot.util.common.StreamUtils;


/**
 * Queries are cached in memory. Size limit is restricted.
 */
public class MemoryCachedQueriesContainer
    implements CachedQueriesContainer
{
    private static final Logger log = Logger.getLogger(MemoryCachedQueriesContainer.class);
    
    /**
     * Maximum cache limit after which cache entries are discarded.
     */
    private final static int DEFAULT_SIZE_LIMIT = 1024 * 1024; //  1mb limit default

    /**
     * Current cache size (total amount of memory consumed by cached queries). 
     */
    private long currentSize = 0;

    private HashMap cache = new HashMap();
    private LinkedList lru = new LinkedList();

    private List listeners = new LinkedList();

    private int sizeLimit = DEFAULT_SIZE_LIMIT;

    public void configure()
    {
    }


    public Iterator getCachedElementSignatures()
    {
        synchronized (this.cache)
        {
            return new ArrayList(lru).iterator();
        }
    }


    public void expungeFromCache(Object signature)
    {
        synchronized (this.cache)
        {
            MemoryCachedQuery cq;

            if ((cq = (MemoryCachedQuery) cache.remove(signature)) != null)
            {
                this.lru.remove(signature);
                this.currentSize -= cq.getDataSize();
                log.debug("Expunging element from cache.");
            }
        }
    }


    public CachedQuery addToCache(CachedQuery q)
    {
        synchronized (this.cache)
        {
            CachedQuery cq;

            if ((cq = (CachedQuery) cache.get(q.getSignature())) != null)
            {
                return cq;
            }

            try
            {
                if (q instanceof MemoryCachedQuery)
                {
                    cq = q;
                }
                else
                {
                    byte [] data = StreamUtils.readFullyAndCloseInput(q.getData());
                    cq = new MemoryCachedQuery(
                            q.getQuery(), q.getComponentId(), q.getOptionalParams(), data
                        );
                }
            }
            catch (IOException e)
            {
                log.error("Cannot create cached query: " + e.toString());

                return cq;
            }

            this.cache.put(cq.getSignature(), cq);
            this.lru.addFirst(cq.getSignature());
            this.currentSize += ((MemoryCachedQuery) cq).getDataSize();

            checkLimits();

            return cq;
        }
    }


    private void checkLimits()
    {
        while ((this.currentSize > sizeLimit) && (this.cache.size() > 0))
        {
            // pick the victim amd remove it.
            Object signature = lru.getLast();
            CachedQuery cq = this.getCachedElement(signature);

            for (Iterator i = this.listeners.iterator(); i.hasNext();)
            {
                ((CacheListener) i.next()).elementRemovedFromCache(cq);
            }

            this.expungeFromCache(signature);
        }
    }


    public CachedQuery getCachedElement(Object signature)
    {
        synchronized (this.cache)
        {
            return (CachedQuery) cache.get(signature);
        }
    }


    public Iterator getCacheListeners()
    {
        synchronized (this.cache)
        {
            return this.listeners.iterator();
        }
    }


    public void addCacheListener(CacheListener l)
    {
        synchronized (this.cache)
        {
            listeners.add(l);
        }
    }


    public void removeCacheListener(CacheListener l)
    {
        synchronized (this.cache)
        {
            listeners.remove(l);
        }
    }


    public void clear() {
        synchronized (this.cache) {
            log.info("Clearing cache.");

            this.cache.clear();
            this.lru.clear();
            this.currentSize = 0;
        }
    }


    public void setConfiguration(Element container) {
        if (container.element("size-limit") != null) {
            this.sizeLimit = Integer.parseInt(container.element("size-limit").getTextTrim());
        }
    }
}