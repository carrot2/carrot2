
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
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
 * Indexes ODP topics according to their category path. Format of the query is
 * simply part of the ODP path, always starting with the Top category, e.g.
 * "Top/Computers/Internet". Additionally, to retrieve documents from
 * recursively from all subcategories, use the star ("*") symbol, e.g.
 * "Top/Computers/Internet/*".
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PathTopicIndex implements TopicIndex, Serializable
{
    /**
     * The tree's top index entry.
     */
    private IndexEntry topEntry;

    /**
     * Creates an empty instance of the index.
     */
    public PathTopicIndex()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.odp.index.TopicIndex#getIds(java.lang.Object)
     */
    public IdIterator getIds(Object query)
    {
        boolean recursive = false;

        // Preprocess the query
        String processedQuery = query.toString();
        if (processedQuery.startsWith("/"))
        {
            processedQuery = processedQuery.substring(1);
        }

        if (processedQuery.endsWith("*"))
        {
            recursive = true;
            processedQuery = processedQuery.substring(0, processedQuery
                .length() - 1);
        }

        if (processedQuery.endsWith("/"))
        {
            processedQuery = processedQuery.substring(0, processedQuery
                .length() - 1);
        }

        // Find the parent node
        IndexEntry parentEntry = lookUp(processedQuery.split("/"), 0, topEntry);

        IdIterator ids;
        if (parentEntry != null)
        {
            if (recursive)
            {
                ids = new DFIndexEntryIterator(parentEntry);
            }
            else
            {
                ids = new SingletonIdIterator(parentEntry.getId());
            }
        }
        else
        {
            ids = new EmptyIdIterator();
        }

        return ids;
    }

    /**
     * Adds a new path to the index. The path must start with the ODP "Top"
     * category.
     * 
     * @param path to be added
     * @param id topic id corresponding to the <code>path<code> given.
     */
    public void add(String path, int id)
    {
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }

        String [] pathElements = path.split("/");
        if (topEntry == null)
        {
            topEntry = new IndexEntry(pathElements[0],
                (pathElements.length == 1 ? id : -1));
        }

        if (pathElements.length > 1)
        {
            add(pathElements, 1, topEntry, id);
        }
    }

    /**
     * @param pathElements
     * @param pos
     * @param entry
     */
    private void add(String [] pathElements, int pos, IndexEntry entry, int id)
    {
        IndexEntry childEntry = entry.getChildEntry(pathElements[pos]);
        if (childEntry == null)
        {
            childEntry = entry.createChildEntry(pathElements[pos], -1);
        }

        if (pos == pathElements.length - 1)
        {
            childEntry.setId(id);
        }
        else
        {
            add(pathElements, pos + 1, childEntry, id);
        }

    }

    private IndexEntry lookUp(String [] pathElements, int pos, IndexEntry entry)
    {
        if (entry == null)
        {
            return null;
        }

        if (pos == pathElements.length - 1)
        {
            return entry;
        }
        else
        {
            IndexEntry newEntry = entry.getChildEntry(pathElements[pos + 1]);
            return lookUp(pathElements, pos + 1, newEntry);
        }
    }

    /**
     * @author Stanislaw Osinski
     * @version $Revision$
     */
    private static class DFIndexEntryIterator implements IdIterator
    {
        /** */
        private IndexEntry indexEntry;

        /** */
        private Iterator childEntries;

        /** */
        private IdIterator childEntryIndexEntryIterator;

        /**
         * @param indexEntry
         */
        public DFIndexEntryIterator(IndexEntry indexEntry)
        {
            this.indexEntry = indexEntry;
            this.childEntries = indexEntry.childEntries();
            if (childEntries.hasNext())
            {
                childEntryIndexEntryIterator = new DFIndexEntryIterator(
                    (IndexEntry) childEntries.next());
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            if (childEntryIndexEntryIterator == null)
            {
                return indexEntry != null && indexEntry.getId() != -1;
            }
            else
            {
                if (childEntryIndexEntryIterator.hasNext())
                {
                    return true;
                }
                else
                {
                    if (childEntries.hasNext())
                    {
                        childEntryIndexEntryIterator = new DFIndexEntryIterator(
                            (IndexEntry) childEntries.next());

                        // The new iterator has to have at least one element
                        return true;
                    }
                    else
                    {
                        // Now it's time to return our data
                        childEntryIndexEntryIterator = null;

                        return indexEntry.getId() != -1;
                    }
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public int next()
        {
            if (childEntryIndexEntryIterator == null)
            {
                int id = indexEntry.getId();

                // Mark the fact that this node's data has been returned
                indexEntry = null;

                return id;
            }
            else
            {
                return childEntryIndexEntryIterator.next();
            }
        }
    }

    /**
     * A single path topic index entry.
     */
    public static class IndexEntry implements Serializable
    {
        /** Child entries */
        private Map childEntries;

        /** Id entry */
        private int id;

        /** Path entry */
        private String pathElement;

        public IndexEntry(String pathElement, int id)
        {
            this.pathElement = pathElement;
            this.id = id;
        }

        /**
         * Returns this PathTopicIndex.IndexEntry's <code>path</code>.
         * 
         */
        public String getPathElement()
        {
            return pathElement;
        }

        /**
         * @param pathElement
         */
        public IndexEntry getChildEntry(String pathElement)
        {
            if (childEntries != null)
            {
                return (IndexEntry) childEntries.get(pathElement);
            }
            else
            {
                return null;
            }
        }

        /**
         * Sets this PathTopicIndex.IndexEntry's <code>id</code>.
         * 
         * @param id
         */
        public void setId(int id)
        {
            this.id = id;
        }

        /**
         * @param pathElement
         * @param id
         */
        public IndexEntry createChildEntry(String pathElement, int id)
        {
            if (childEntries == null)
            {
                childEntries = new TreeMap();
            }
            IndexEntry indexEntry = new IndexEntry(pathElement, id);
            childEntries.put(pathElement, indexEntry);
            return indexEntry;
        }

        /**
         */
        public Iterator childEntries()
        {
            if (childEntries != null)
            {
                return childEntries.values().iterator();
            }
            else
            {
                return Collections.EMPTY_LIST.iterator();
            }
        }

        /**
         * Returns this CatidPrimaryTopicIndexBuilder.CatidIndexEntry's
         * <code>id</code>.
         * 
         */
        public int getId()
        {
            return id;
        }
    }

    public void serialize(OutputStream outputStream) throws IOException
    {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
            new GZIPOutputStream(outputStream));
        serializeIndexEntry(topEntry, objectOutputStream);
        objectOutputStream.close();
    }

    private void serializeIndexEntry(IndexEntry indexEntry,
        ObjectOutputStream objectOutputStream) throws IOException
    {
        Map childEntries = indexEntry.childEntries;
        if (childEntries != null)
        {
            objectOutputStream.writeInt(childEntries.size());
            for (Iterator iter = childEntries.values().iterator(); iter
                .hasNext();)
            {
                IndexEntry childEntry = (IndexEntry) iter.next();
                serializeIndexEntry(childEntry, objectOutputStream);
            }
        }
        else
        {
            objectOutputStream.writeInt(0);
        }

        objectOutputStream.writeUTF(indexEntry.getPathElement());
        objectOutputStream.writeInt(indexEntry.getId());
    }

    public void deserialize(InputStream inputStream) throws IOException
    {
        ObjectInputStream objectInputStream = new ObjectInputStream(
            new GZIPInputStream(inputStream));
        topEntry = deserializeIndexEntry(objectInputStream);
    }

    private IndexEntry deserializeIndexEntry(ObjectInputStream objectInputStream)
        throws IOException
    {
        int childCount = objectInputStream.readInt();

        IndexEntry indexEntry;
        if (childCount == 0)
        {
            indexEntry = new IndexEntry(objectInputStream.readUTF(),
                objectInputStream.readInt());
        }
        else
        {
            Map deserializedChildEntries = new TreeMap();
            for (int i = 0; i < childCount; i++)
            {
                IndexEntry childEntry = deserializeIndexEntry(objectInputStream);
                deserializedChildEntries
                    .put(childEntry.pathElement, childEntry);
            }

            indexEntry = new IndexEntry(objectInputStream.readUTF(),
                objectInputStream.readInt());
            indexEntry.childEntries = deserializedChildEntries;
        }

        return indexEntry;
    }
}