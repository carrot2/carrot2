/*
 * PrimaryTopicIndexBuilder.java
 * 
 * Created on 2004-06-25
 */
package com.stachoodev.carrot.odp.index;

import java.io.*;

/**
 * Defines the interface of an ODP primary topic index.
 * 
 * @author stachoo
 */
public interface PrimaryTopicIndexBuilder
{
    /**
     * Creates a {@link PrimaryTopicIndex}for given ODP RDF content data. This
     * method must also create the underlying file and directory structure.
     * 
     * @param inputStream an {@link InputStream}associated with the ODP RDF
     *            content file to be indexed.
     * @param indexDataLocation the location at which all index data can be
     *            stored
     * @throws IOException
     * @return
     */
    public PrimaryTopicIndex create(InputStream inputStream,
        String indexDataLocation) throws IOException;
}