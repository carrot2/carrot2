
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

package org.carrot2.output.metrics;

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Computes {@link #documentCoverage}, {@link #topicCoverage} and
 * {@link #absoluteTopicCoverage} metrics for the current set of clusters. For full
 * definitions, please see section 4.4.1 of <a
 * href="http://project.carrot2.org/publications/osinski04-dimensionality.pdf">this
 * work</a>.
 * <p>
 * The coverage metrics are calculated for top-level clusters only, taking into account
 * documents from the cluster and all subclusters. Finally, coverage metrics will be
 * calculated only if all input documents have non-blank {@link Document#TOPIC}s.
 * </p>
 */
@Bindable
public class CoverageMetric implements ClusteringMetric
{
    /**
     * Absolute topic coverage. Absolute topic coverage will be equal to 1.0 for a set of
     * clusters where each of the N input topics will have exactly one corresponding
     * cluster among the first N top clusters.
     */
    @Processing
    @Output
    @Attribute(key = "topic-coverage-absolute")
    public double absoluteTopicCoverage;

    /**
     * Topic coverage. Topic coverage will be equal to 1.0 for a set of clusters where
     * each of the N input topic is represented by at least one cluster, no matter the
     * position of the cluster on the list of clusters.
     */
    @Processing
    @Output
    @Attribute(key = "topic-coverage")
    public double topicCoverage;

    /**
     * Document coverage. Document coverage is the proportion of documents assigned to
     * non-Other Topics clusters to all clustered documents. If the Other Topics cluster
     * is empty, document coverage is equal to 1.0.
     */
    @Processing
    @Output
    @Attribute(key = "document-coverage")
    public double documentCoverage;

    @Processing
    @Input
    @Attribute
    public boolean enabled = true;

    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    @Processing
    @Input
    @Attribute(key = AttributeNames.CLUSTERS)
    public List<Cluster> clusters;

    public void calculate()
    {
        final int topicCount = ClusteringMetricUtils.countTopics(documents);
        if (topicCount == 0)
        {
            return;
        }

        if (clusters.size() == 0)
        {
            return;
        }

        int p = 0;
        final Set<String> coveredTopics = Sets.newHashSet();
        final Set<Document> coveredDocuments = Sets.newHashSet();
        for (Cluster cluster : clusters)
        {
            if (cluster.getAttribute(Cluster.OTHER_TOPICS) != null)
            {
                continue;
            }

            coveredDocuments.addAll(cluster.getAllDocuments());
            coveredTopics.add(calculateMajorityTopic(cluster));
            p++;
            if (coveredTopics.size() == topicCount)
            {
                break;
            }
        }

        absoluteTopicCoverage = coveredTopics.size() / Math.sqrt(Math.max(topicCount, p) * topicCount);
        topicCoverage = coveredTopics.size() / (double) topicCount;
        documentCoverage = coveredDocuments.size() / (double) documents.size();
    }

    private String calculateMajorityTopic(Cluster cluster)
    {
        final List<Document> allDocuments = cluster.getAllDocuments();
        final Map<String, Integer> counts = Maps.newHashMap();
        Integer max = 0;
        String majorityTopic = null;
        for (Document document : allDocuments)
        {
            final String topic = document.getField(Document.TOPIC);
            final Integer newValue = MapUtils.increment(counts, topic);
            if (newValue > max)
            {
                max = newValue;
                majorityTopic = topic;
            }
        }

        return majorityTopic;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
