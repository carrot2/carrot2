
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

package org.carrot2.demo.cache;

import java.util.LinkedList;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import org.carrot2.core.impl.ArrayOutputComponent.Result;

/**
 * A cache based on weak references. However, last few requests
 * are always stored (hard references).
 * 
 * @author Dawid Weiss
 */
final class WeakRawDocumentsCache implements RawDocumentsCache {
    private final Logger logger = Logger.getLogger(WeakRawDocumentsCache.class); 

    private final WeakHashMap cache = new WeakHashMap();
    private final LinkedList hardCache;
    private final int hardCacheSize;

    public WeakRawDocumentsCache(int hardCacheSize) {
        this.hardCacheSize = hardCacheSize;
        this.hardCache = new LinkedList();
    }
    
    public synchronized Result get(CacheEntry entry) {
        final Result result = (Result) cache.get(entry); 
        if (result != null) {
            logger.info("Returning cached data for: " + entry);
            updateRecent(entry);
        } else {
            logger.info("Not in cache: " + entry);
        }
        return result;
    }

    public synchronized void put(CacheEntry entry, Result result) {
        logger.info("Saving cached data for: " + entry);
        cache.put(entry, result);
        updateRecent(entry);
    }

    private void updateRecent(CacheEntry entry) {
        final int index = hardCache.indexOf(entry);
        if (index >= 0) {
            final CacheEntry cachedKey = (CacheEntry) hardCache.remove(index);
            hardCache.addFirst(cachedKey);
        } else {
            hardCache.addFirst(entry);
            while (hardCache.size() > hardCacheSize) {
                hardCache.removeLast();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Hard cache: " + hardCache);
        }
    }
}
