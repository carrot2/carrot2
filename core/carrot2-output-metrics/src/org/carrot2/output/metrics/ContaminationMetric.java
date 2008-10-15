package org.carrot2.output.metrics;

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.MapUtils;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Computes cluster contamination. If a cluster groups documents with the same
 * {@link Document#TOPIC}, its contamination is 0. If a cluster groups an equally
 * distributed mix of all topics, its contamination is 1.0. For a full definition, please
 * see section 4.4.1 of <a
 * href="http://project.carrot2.org/publications/osinski04-dimensionality.pdf">this
 * work</a>.
 * <p>
 * Contamination is calculated for top-level clusters only, taking into account documents
 * from the cluster and all subclusters. Finally, contamination will be calculated only if
 * all input documents have non-blank {@link Document#TOPIC}s.
 * </p>
 */
@Bindable
public class ContaminationMetric implements ClusteringMetric
{
    /**
     * Key for the contamination value of a cluster.
     */
    public static final String CONTAMINATION = "contamination";

    /**
     * Average contamination of the whole cluster set, weighted by the size of cluster.
     */
    @Processing
    @Output
    @Attribute(key = "average-contamination")
    public double weightedAverageContamination;

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

        int weightSum = 0;
        double contaminationSum = 0;

        for (Cluster cluster : clusters)
        {
            if (cluster.getAttribute(Cluster.OTHER_TOPICS) != null)
            {
                continue;
            }
            final double contamination = calculate(cluster, topicCount);
            cluster.setAttribute(CONTAMINATION, contamination);

            contaminationSum += contamination * cluster.size();
            weightSum += cluster.size();
        }

        weightedAverageContamination = contaminationSum / weightSum;
    }

    static double calculate(Cluster cluster, int topicCount)
    {
        final double worstCaseH = calculateWorstCaseH(cluster.size(), topicCount);
        if (worstCaseH == 0)
        {
            return 0;
        }
        else
        {
            return calculateH(cluster) / worstCaseH;
        }
    }

    static int calculateH(Cluster cluster)
    {
        final Map<String, Integer> documentCountByTopic = Maps.newHashMap();
        final List<Document> allDocuments = cluster.getAllDocuments();
        for (Document document : allDocuments)
        {
            MapUtils.increment(documentCountByTopic, (String) document
                .getField(Document.TOPIC));
        }

        final ArrayList<Integer> counts = Lists.newArrayList();
        counts.addAll(documentCountByTopic.values());

        return calculateH(counts);
    }

    static int calculateWorstCaseH(int clusterSize, int topicCount)
    {
        final ArrayList<Integer> counts = Lists.newArrayList();

        for (int topic = 0; topic < topicCount; topic++)
        {
            counts.add(clusterSize / topicCount
                + (topic < (clusterSize % topicCount) ? 1 : 0));
        }

        return calculateH(counts);
    }

    static int calculateH(final ArrayList<Integer> counts)
    {
        int h = 0;
        for (int i = 0; i < counts.size() - 1; i++)
        {
            for (int j = i + 1; j < counts.size(); j++)
            {
                h += counts.get(i) * counts.get(j);
            }
        }
        return h;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
