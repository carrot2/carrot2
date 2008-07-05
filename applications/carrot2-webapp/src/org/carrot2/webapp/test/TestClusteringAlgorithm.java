package org.carrot2.webapp.test;

import java.util.Collections;
import java.util.List;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;

/**
 * A clustering "algorithm" that generates clustering with certain properties. Useful only
 * for application testing purposes.
 */
@Bindable
public class TestClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{
    /**
     * {@link Document}s to cluster.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents = Collections.<Document> emptyList();

    /**
     * {@link Cluster}s created by the algorithm.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.CLUSTERS)
    public List<Cluster> clusters = null;

    @Processing
    @Input
    @Attribute(key = "depth")
    public int depth = 2;

    @Processing
    @Input
    @Attribute(key = "rootClusters")
    public Integer rootClusters = 20;

    @Processing
    @Input
    @Attribute(key = "childClusters")
    public int childClusters = 5;

    @Processing
    @Input
    @Attribute(key = "documentsPerCluster")
    public int documentsPerCluster = 2;

    /**
     * Performs test clustering.
     */
    public void process() throws ProcessingException
    {
        clusters = Lists.newArrayList();

        for (int i = 0; i < rootClusters; i++)
        {
            final Cluster cluster = new Cluster("Cluster " + (i + 1));
            clusters.add(cluster);

            if (depth > 1)
            {
                addClusters(cluster, depth - 1);
            }

            addDocuments(cluster, 0);
        }
    }

    private void addClusters(Cluster parent, int level)
    {
        for (int i = 0; i < childClusters; i++)
        {
            final Cluster cluster = new Cluster("Cluster " + (i + 1));
            parent.addSubclusters(cluster);

            if (level > 1)
            {
                addClusters(cluster, level - 1);
            }
        }
    }

    private int addDocuments(Cluster cluster, int documentIndex)
    {
        for (int i = 0; i < documentsPerCluster; i++)
        {
            cluster.addDocuments(documents.get((documentIndex++) % documents.size()));
        }

        for (Cluster subcluster : cluster.getSubclusters())
        {
            documentIndex = addDocuments(subcluster, documentIndex);
        }
        
        return documentIndex;
    }
}
