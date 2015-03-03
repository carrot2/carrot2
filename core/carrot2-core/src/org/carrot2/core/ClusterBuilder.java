package org.carrot2.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Assists in building up a {@link Cluster}. 
 */
public final class ClusterBuilder extends AttributeSetBuilder<Cluster>
{
    public ClusterBuilder id(int id) { return attr(Cluster.ID, id); }
    public ClusterBuilder markAsOtherTopics() { return attr(Cluster.OTHER_TOPICS, true); }
    public ClusterBuilder score(Double score) { return attr(Cluster.SCORE, score); }

    private List<String> phrases;
    public ClusterBuilder phrases(Collection<String> phrases) { return attr(Cluster.PHRASES, phrases = new ArrayList<>(phrases)); }
    public ClusterBuilder phrase(String phrase)
    {
        if (phrases == null) {
            phrases(Collections.<String> emptyList());
        }
        phrases.add(phrase);
        return this;
    }

    private List<Document> documents;
    public ClusterBuilder documents(Collection<Document> docs) { return attr(Cluster.DOCUMENTS, documents = new ArrayList<>(docs)); }
    public ClusterBuilder document(Document doc)
    {
        if (documents == null) {
            documents(Collections.<Document> emptyList());
        }
        documents.add(doc);
        return this;
    }

    private List<Cluster> subclusters;
    public ClusterBuilder subclusters(Collection<Cluster> clusters) { return attr(Cluster.CLUSTERS, new ArrayList<>(clusters)); }
    public ClusterBuilder subcluster(Cluster cluster)
    {
        if (subclusters == null) {
            subclusters(Collections.<Cluster> emptyList());
        }
        subclusters.add(cluster);
        return this;
    }

    @Override
    protected ClusterBuilder attr(String key, Object value)
    {
        return (ClusterBuilder) super.attr(key, value);
    }

    @Override
    public Cluster build()
    {
        return new Cluster(cloneAndClearAttributes());
    }
}