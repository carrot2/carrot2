

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.utils;


import java.util.HashMap;
import java.util.Vector;


/**
 * optimized for put, not for get operations
 *
 * @author Micha� Wr�blewski
 */
public class FrequencyHashMap
{
    class MapEntry
    {
        Object item;
        int frequency;

        MapEntry(Object item, int frequency)
        {
            this.item = item;
            this.frequency = frequency;
        }

        public boolean equals(Object o)
            throws ClassCastException
        {
            MapEntry entry = (MapEntry) o;

            return (item.equals(entry.item));
        }
    }

    protected HashMap map;

    public FrequencyHashMap()
    {
        map = new HashMap();
    }

    public void put(Object key, Object value)
    {
        Vector storedValue = (Vector) map.get(key);

        if (storedValue == null)
        {
            storedValue = new Vector();
            storedValue.add(new MapEntry(value, 1));
            map.put(key, storedValue);
        }
        else
        {
            int valueIndex = -1;

            for (int i = 0; i < storedValue.size(); i++)
            {
                MapEntry e = (MapEntry) storedValue.elementAt(i);

                if (e.item.equals(value))
                {
                    valueIndex = i;

                    break;
                }
            }

            if (valueIndex == -1)
            {
                storedValue.add(new MapEntry(value, 1));
            }
            else
            {
                ((MapEntry) storedValue.elementAt(valueIndex)).frequency++;
            }
        }
    }


    public Object get(Object key)
    {
        Vector storedValue = (Vector) map.get(key);

        if (storedValue == null)
        {
            return null;
        }

        MapEntry mostFrequentValue = (MapEntry) storedValue.elementAt(0);

        for (int i = 1; i < storedValue.size(); i++)
        {
            MapEntry e = (MapEntry) storedValue.elementAt(i);

            if (e.frequency > mostFrequentValue.frequency)
            {
                mostFrequentValue = e;
            }
        }

        return mostFrequentValue.item;
    }
}
