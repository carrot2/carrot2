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

import com.stachoodev.util.common.*;

/**
 * Defines the interface of an ODP primary topic index.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface PrimaryTopicIndexBuilder extends PropertyProvider
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