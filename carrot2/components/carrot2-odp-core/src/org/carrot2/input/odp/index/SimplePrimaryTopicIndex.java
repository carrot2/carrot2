
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.odp.index;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * An array-based implementation of the
 * {@link org.carrot2.input.odp.index.PrimaryTopicIndex}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SimplePrimaryTopicIndex implements PrimaryTopicIndex, Serializable
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

    /**
     *  
     */
    public SimplePrimaryTopicIndex()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.PrimaryTopicIndex#getLocation(java.lang.String)
     */
    public Location getLocation(int id)
    {
        int index = Collections.binarySearch(indexEntries, new IndexEntry(id,
            null));
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
     * @see org.carrot2.input.odp.index.PrimaryTopicIndex#getAllLocations()
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
    public static class IndexEntry implements Comparable, Serializable
    {
        /** Location entry */
        private Location location;

        /** Catid entry */
        private int id;

        public IndexEntry(int id, Location location)
        {
            this.id = id;
            this.location = location;
        }

        /**
         * Returns this CatidPrimaryTopicIndexBuilder.CatidIndexEntry's
         * <code>catid</code>.
         */
        public int getId()
        {
            return id;
        }

        /**
         * Returns this CatidPrimaryTopicIndexBuilder.CatidIndexEntry's
         * <code>location</code>.
         */
        public Location getLocation()
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

            return id == ((IndexEntry) obj).id
                && location.equals(((IndexEntry) obj).location);
        }

        public int compareTo(Object o)
        {
            // Don't bother checking type - we would throw ClassCastException
            // anyway
            IndexEntry entry = (IndexEntry) o;

            if (id > entry.id)
            {
                return 1;
            }
            else if (id < entry.id)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.TopicIndex#serialize(java.io.OutputStream)
     */
    public void serialize(OutputStream outputStream) throws IOException
    {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
            new GZIPOutputStream(outputStream));
        objectOutputStream.writeInt(indexEntries.size());
        for (Iterator iter = indexEntries.iterator(); iter.hasNext();)
        {
            IndexEntry indexEntry = (IndexEntry) iter.next();
            indexEntry.getLocation().serialize(objectOutputStream);
            objectOutputStream.writeInt(indexEntry.getId());
        }
        objectOutputStream.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.TopicIndex#deserialize(java.io.InputStream)
     */
    public void deserialize(InputStream inputStream,
        LocationFactory locationFactory) throws IOException
    {
        ObjectInputStream objectInputStream = new ObjectInputStream(
            new GZIPInputStream(inputStream));
        int size = objectInputStream.readInt();
        indexEntries = new ArrayList(size);

        for (int i = 0; i < size; i++)
        {
            Location location = locationFactory.createLocation();
            location.deserialize(objectInputStream);

            indexEntries.add(new IndexEntry(objectInputStream.readInt(),
                location));
        }
    }
}