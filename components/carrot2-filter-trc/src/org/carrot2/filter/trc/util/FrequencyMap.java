
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

package org.carrot2.filter.trc.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Comparator;


/**
 * Map of Object -> its frequency.
 * Essentially a wrapper for Map (Object -> Integer) for easier operations.
 *
 */
public class FrequencyMap {


    private Map entries;

    public FrequencyMap() {
        this.entries = new HashMap();
    }

    /**
     * Add object to map.
     * If object already exist (check is based on Object.equals() method), its frequency is increased;
     * otherwise new frequency entry is created for object.
     *
     * @param object object to be added.
     * @return <code>true</code> if object was not this map already.
     */
    public boolean add(Object object) {
        if (entries.containsKey(object)) {
            entries.put(object, new Integer(((Integer)entries.get(object)).intValue() + 1));
            return false;
        } else {
            entries.put(object, new Integer(1));
            return true;
        }
    }

    /**
     * Add object i times
     * (in other words : increase frequency of given object in this map by i)
     * @param object
     * @param i number of times given object is added. Must be greater than 0.
     * @return <code>true</code> if object was not in this map already
     */
    public boolean add(Object object, int i) {
        if (i > 0) {
            int count = getFrequency(object);
            entries.put(object, new Integer(count + i));
            return (count < 0);
        }
        return false;
    }

    /**
     * Return frequency of given object in this map
     * @param object
     * @return frequency (number of times object has been added) of object in this map.
     * Return 0 if object doesn't exist in this map.
     */
    public int getFrequency(Object object) {
        Integer count = (Integer) entries.get(object);
        return count == null ? 0 : count.intValue();
    }

    /**
     * Return internal map
     */
    public Map getInternalMap() {
        return entries;
    }

    /**
     * Merge with another frequency map.
     * Frequency for common objects are cummulated.
     * @param map
     */
    public void merge(FrequencyMap map) {
        for (Iterator iterator = map.getInternalMap().entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            add(entry.getKey(), ((Integer)entry.getValue()).intValue());
        }
    }


    /**
     * Add all objects from given collection
     * @param objects
     */
    public void addAll(Collection objects) {
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            add(iterator.next());
        }
    }

    /**
     * Clear this map. All entries are deleted
     */
    public void clear() {
        entries.clear();
    }

    /**
     * Return set of keys
     */
    public Set keySet() {
        return entries.keySet();
    }

    public SortedMap getDescendingSorted() {
        SortedMap sorted = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                return getFrequency(o1) > getFrequency(o2) ? -1 : 1;
            }
        });
        sorted.putAll(entries);
        return sorted;
    }

    public SortedMap getAscendingSorted() {
        SortedMap sorted = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                return getFrequency(o1) > getFrequency(o2) ? 1 : -1;
            }
        });
        sorted.putAll(entries);
        return sorted;
    }
    public String toString() {
        return entries.toString();
    }
}
