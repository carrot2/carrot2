
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
import java.util.*;

import org.carrot2.input.odp.Topic;
import org.carrot2.input.odp.TopicSerializer;
import org.carrot2.input.odp.index.*;
import org.carrot2.tools.odp.common.ODPAbstractSaxHandler;

/**
 * Builds a {@link CatidPrimaryTopicIndexBuilder}based on the ODP Topic's
 * <code>catid</code> attribute. Indices created by this class are instances
 * of {@link CatidPrimaryTopicIndexBuilder}.
 * 
 * Content files (one file contains one topic along with all its external pages)
 * are laid out in a hierarchical structure of file system directories
 * corresponding to topic 'paths' in the original ODP structure, e.g.
 * Top/World/Poland/Komputery. The maximum depth of the file system directory
 * structure can be specified beyond which all topics will be saved in a flat
 * list of files (file name is the topics <code>catid</code>). As ODP topic
 * 'paths' can contain problematic UTF8 characters, each element of the path is
 * mapped to an integer number.
 * 
 * This index builder is <b>not </b> thread-safe.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class CatidPrimaryTopicIndexBuilder extends ODPAbstractSaxHandler implements
    PrimaryTopicIndexBuilder
{
    /** Topic serializer */
    private TopicSerializer topicSerializer;

    /** Entries of this index */
    private List indexEntries;

    /** Piggyback topic index builders */
    private Collection topicIndexBuilders;

    /*
     * (non-Javadoc)
     */
    public PrimaryTopicIndex create(InputStream inputStream,
        TopicSerializer topicSerializer, Collection topicIndexBuilders)
        throws IOException, ClassNotFoundException
    {
        this.topicIndexBuilders = topicIndexBuilders;
        this.topicSerializer = topicSerializer;

        // Reset fields
        indexEntries = new ArrayList();
        initalizeParser(inputStream);

        // Sort the entries according to the id and convert to a
        // PrimaryTopicIndex
        Collections.sort(indexEntries);
        return new SimplePrimaryTopicIndex(indexEntries);
    }

    /**
     * @param topic
     * @throws IOException
     */
    protected void index(Topic topic) throws IOException
    {
        // Omit empty topics
        if (topic.getExternalPages().size() == 0)
        {
            return;
        }

        // Serialize the topic
        Location location = topicSerializer.serialize(topic);

        // Add to index entries
        indexEntries.add(new SimplePrimaryTopicIndex.IndexEntry(topic
            .getCatid(), location));

        // Let the piggyback topic index builders
        if (topicIndexBuilders != null)
        {
            for (Iterator iter = topicIndexBuilders.iterator(); iter.hasNext();)
            {
                TopicIndexBuilder topicIndexBuilder = (TopicIndexBuilder) iter
                    .next();
                topicIndexBuilder.index(topic);
            }
        }

        // Fire the event
        fireTopicIndexed();
    }

    /**
     * Sets this CatidPrimaryTopicIndexBuilder's <code>topicSerializer</code>.
     * 
     * @param topicSerializer
     */
    public void setTopicSerializer(TopicSerializer topicSerializer)
    {
        this.topicSerializer = topicSerializer;
    }
}