
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.local.controller;

import java.util.*;


/**
 * A wrapper around a Map that does not change it, even
 * if new values are saved to it.
 * 
 * <p>Values from the fallback map can be overriden, but cannot
 * be removed from the map.
 * 
 * <p>This class should be reimplemented in the future
 * to take less memory (reuse the fallback map). 
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public final class FallbackMap implements Map {

    /**
     * The fallback map.
     */
    private Map fallbackMap;

    /**
     * The primary map.
     */
    private Map rwMap;

    /**
     * Creates a new FallbackMap object.
     *
     * @param fallbackMap The fallback map to use.
     */
    public FallbackMap(Map fallbackMap) {
    	  if (fallbackMap == null)
    	    fallbackMap = java.util.Collections.EMPTY_MAP;
        this.rwMap = new HashMap( fallbackMap );
    }

    public int size() {
        return rwMap.size();
    }

    /**
     * See Map interface for details.
     */
    public void clear() {
        rwMap.clear();
        rwMap.putAll(fallbackMap);
    }

    /**
     * See Map interface for details.
     */
    public boolean isEmpty() {
        return (this.size() == 0);
    }

    /**
     * See Map interface for details.
     */
    public boolean containsKey(Object key) {
        if ((rwMap != null) && rwMap.containsKey(key)) {
            return true;
        }

        return false;
    }

    /**
     * See Map interface for details.
     */
    public boolean containsValue(Object value) {
        if ((rwMap != null) && rwMap.containsValue(value)) {
            return true;
        }

        return false;
    }

    /**
     * See Map interface for details.
     */
    public Collection values() {
        return rwMap.values();
    }

    /**
     * See Map interface for details.
     */
    public void putAll(Map t) {
        this.rwMap.putAll(t);
    }

    /**
     * This operation is unsupported on Fallback map.
     */
    public Set entrySet() {
        return rwMap.entrySet();
    }

    /**
     * See Map interface for details.
     */
    public Set keySet() {
        return rwMap.keySet();
    }

    /**
     * See Map interface for details.
     */
    public Object get(Object key) {
        return rwMap.get(key);
    }

    /**
     * See Map interface for details.
     */
    public Object remove(Object key) {
        if (this.fallbackMap.containsKey(key))
            return null;
        return rwMap.remove(key);
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        return rwMap.put(key,value);
    }
}
