

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


import java.util.Iterator;


/**
 * Cache container interface. Containters hold cached queries.
 */
public interface CachedQueriesContainer
{
    /**
     * Invoked when all properties have been set to ensure the component is valid.
     */
    public void configure()
        throws RuntimeException;


    public Iterator getCachedElementSignatures();


    public CachedQuery getCachedElement(Object signature);


    public CachedQuery addToCache(CachedQuery q);


    public void expungeFromCache(Object signature);


    public void addCacheListener(CacheListener l);


    public void removeCacheListener(CacheListener l);


    public /* CacheListener */ Iterator getCacheListeners();
}
