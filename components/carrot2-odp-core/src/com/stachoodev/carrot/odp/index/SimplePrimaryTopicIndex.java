/*
 * SimplePrimaryTopicIndex.java
 * 
 * Created on 2004-06-25
 */
package com.stachoodev.carrot.odp.index;

import java.util.*;

/**
 * An array-based implementation of the
 * {@link com.stachoodev.carrot.odp.index.PrimaryTopicIndex}interface.
 * 
 * @author stachoo
 */
public class SimplePrimaryTopicIndex implements PrimaryTopicIndex
{
    /** An ordered list of IndexEntries */
    private List indexEntries;

    /**
     * Creates a new {@link SimplePrimaryTopicIndex}.
     * 
     * @param indexEntries a sorted list of {@link IndexEntry}objects. The list
     *            must not be <code>null</code>, but can be empty.
     */
    public SimplePrimaryTopicIndex(List indexEntries)
    {
        if (indexEntries == null)
        {
            throw new IllegalArgumentException(
                "The indexEntries parameter must not be null");
        }

        this.indexEntries = indexEntries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.PrimaryTopicIndex#getLocation(java.lang.String)
     */
    public String getLocation(String id)
    {
        int index = Collections.binarySearch(indexEntries, new IndexEntry(id,
            ""));
        if (index < 0)
        {
            return null;
        }
        else
        {
            return ((IndexEntry) indexEntries.get(index)).getLocation();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.PrimaryTopicIndex#getAllLocations()
     */
    public Iterator getAllLocations()
    {
        return new LocationIterator(indexEntries.iterator());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (obj.getClass() != getClass())
        {
            return false;
        }

        return indexEntries
            .equals(((SimplePrimaryTopicIndex) obj).indexEntries);
    }

    /**
     * @author stachoo
     */
    private class LocationIterator implements Iterator
    {
        /** The underlying indexEntries iterator */
        private Iterator indexEntriesIterator;

        /**
         * @param indexEntriesIterator
         */
        public LocationIterator(Iterator indexEntriesIterator)
        {
            this.indexEntriesIterator = indexEntriesIterator;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#remove()
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            return indexEntriesIterator.hasNext();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public Object next()
        {
            IndexEntry entry = (IndexEntry) indexEntriesIterator.next();
            return entry.getLocation();
        }
    }

    /**
     * A single catid index entry.
     * 
     * @author stachoo
     */
    public static class IndexEntry implements Comparable
    {
        /** Location entry */
        private String location;

        /** Catid entry */
        private String id;

        /**
         * @param catid
         * @param location
         */
        public IndexEntry(String id, String location)
        {
            this.id = id;
            this.location = location;
        }

        /**
         * Returns this CatidPrimaryTopicIndexBuilder.CatidIndexEntry's
         * <code>catid</code>.
         * 
         * @return
         */
        public String getId()
        {
            return id;
        }

        /**
         * Returns this CatidPrimaryTopicIndexBuilder.CatidIndexEntry's
         * <code>location</code>.
         * 
         * @return
         */
        public String getLocation()
        {
            return location;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }

            if (obj == null)
            {
                return false;
            }

            if (obj.getClass() != getClass())
            {
                return false;
            }

            return id.equals(((IndexEntry) obj).id)
                && location.equals(((IndexEntry) obj).location);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o)
        {
            // Don't bother checking type - we would throw ClassCastException
            // anyway
            IndexEntry entry = (IndexEntry) o;

            return id.compareTo(entry.id);
        }
    }
}