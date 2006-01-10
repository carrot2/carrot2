
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


import java.util.Iterator;

import org.dom4j.Element;


/**
 * Cache container interface. Containters hold cached queries.
 */
public interface CachedQueriesContainer
{
    /**
     * Invoked when all properties have been set to ensure the component is valid.
     */
    public void configure();


    public Iterator getCachedElementSignatures();


    public CachedQuery getCachedElement(Object signature);


    public CachedQuery addToCache(CachedQuery q);


    public void expungeFromCache(Object signature);


    public void addCacheListener(CacheListener l);


    public void removeCacheListener(CacheListener l);


    /**
     * Returns an iterator of {@link CacheListener} objects.
     */
    public Iterator getCacheListeners();


    /** Clears all queries in this cache container if it is read-write */
    public void clear();

    /**
     * Configure container using DOM4J XML (each container will have its specific configuration settings). 
     */
    public void setConfiguration(Element container);
}
