
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

package org.carrot2.tools.odp.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.carrot2.input.odp.TopicSerializer;
import org.carrot2.input.odp.index.PrimaryTopicIndex;
import org.carrot2.util.PropertyProvider;


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
     * @param topicSerializer a {@link TopicSerializer}to be used to store
     *            topic data
     * @param topicIndexBuilders a collection of {@link TopicIndexBuilder}s to
     *            be executed after a primary index entry has been created for a
     *            topic
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public PrimaryTopicIndex create(InputStream inputStream,
        TopicSerializer topicSerializer, Collection topicIndexBuilders)
        throws IOException, ClassNotFoundException;
}