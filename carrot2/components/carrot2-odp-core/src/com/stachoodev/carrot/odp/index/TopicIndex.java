/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp.index;

import java.io.*;

/**
 * Defines the interface of an ODP topic index.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TopicIndex
{
    /**
     * Returns an iterator of identifiers of files containing topics specified
     * in the query. The identifiers can be used to fetch ODP topics from the
     * primary index. If no ids have been identified for given query, the
     * iterator must be empty.
     * 
     * @param query
     * @return
     */
    public IdIterator getIds(Object query);

    /**
     * Serializes this index to the {@link OutputStream}given.
     * 
     * @param outputStream
     * @throws IOException
     */
    public void serialize(OutputStream outputStream) throws IOException;

    /**
     * Deserializes contents of this index from the {@link InputStream}given.
     * 
     * @param inputStream
     * @throws IOException
     */
    public void deserialize(InputStream inputStream) throws IOException;
}