
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.output.metrics;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.*;

import com.google.common.collect.*;

/**
 *
 */
@Bindable
public abstract class IdealPartitioningBasedClusteringMetric implements
    IClusteringMetric
{
    /**
     * Partition id field name.
     */
    @Input
    @Processing
    @Attribute
    public String partitionIdFieldName = Document.TOPIC;

    /**
     * Key for the major {@link Document#TOPIC} of a cluster.
     */
    public static final String MAJOR_TOPIC = "major-topic";

    /**
     * Returns the number of distinct {@link Document#TOPIC}s in a collection of
     * documents. Note if that at least one of the document has a <code>null</code> topic,
     * 0 will be returned.
     */
    int getTopicCount(List<Document> documents)
    {
        final HashSet<String> topics = Sets.newHashSet();
        for (Document document : documents)
        {
            final String topic = document.getField(partitionIdFieldName);
            if (StringUtils.isBlank(topic))
            {
                return 0;
            }
            topics.add(topic);
        }
        return topics.size();
    }

    /**
     * Returns the the topic represented by the majority of the provided documents.
     */
    String getMajorTopic(List<Document> documents)
    {
        final Map<String, Integer> counts = Maps.newHashMap();
        Integer max = 0;
        String majorityTopic = null;
        for (Document document : documents)
        {
            final String topic = document.getField(partitionIdFieldName);
            final Integer newValue = MapUtils.increment(counts, topic);
            if (newValue > max)
            {
                max = newValue;
                majorityTopic = topic;
            }
        }

        return majorityTopic;
    }

    /**
     * Returns documents grouped by topics.
     */
    Map<String, Set<Document>> getDocumentsByTopic(List<Document> documents)
    {
        final Multimap<String, Document> result = Multimaps.newHashMultimap();
        for (Document document : documents)
        {
            final String topic = document.getField(partitionIdFieldName);
            if (topic != null)
            {
                result.put(topic, document);
            }
        }

        final Map<String, Set<Document>> map = Maps.newHashMap();
        for (String key : result.keySet())
        {
            map.put(key, Sets.newHashSet(result.get(key)));
        }

        return map;
    }

    /**
     * Returns document counts for each topic.
     */
    Map<String, Integer> getDocumentCountByTopic(List<Document> documents)
    {
        final Map<String, Integer> result = Maps.newHashMap();
        final Map<String, Set<Document>> documentsByTopic = getDocumentsByTopic(documents);

        for (Map.Entry<String, Set<Document>> set : documentsByTopic.entrySet())
        {
            result.put(set.getKey(), set.getValue().size());
        }

        return result;
    }
}
