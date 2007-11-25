package org.carrot2.core;

import java.util.*;

import org.carrot2.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Comparators;

public class Cluster
{
    public static final String OTHER_TOPICS = "other-topics";

    private List<String> phrases = new ArrayList<String>();
    private List<String> phrasesView = Collections.unmodifiableList(phrases);

    private List<Cluster> subclusters = new ArrayList<Cluster>();
    private List<Cluster> subclustersView = Collections.unmodifiableList(subclusters);

    private List<Document> documents = new ArrayList<Document>();
    private List<Document> documentsView = Collections.unmodifiableList(documents);

    private Map<String, Object> attributes = new HashMap<String, Object>();

    /** Cached concatenated label */
    private String labelCache = null;

    /** Cached actual cluster size */
    private int actualSizeCache = -1;

    public Cluster()
    {
    }

    public String getLabel()
    {
        if (labelCache == null)
        {
            labelCache = StringUtils.toString(phrases, ", ");
        }
        return labelCache;
    }

    public List<String> getPhrases()
    {
        return phrasesView;
    }

    public List<Cluster> getSubclusters()
    {
        return subclustersView;
    }

    public List<Document> getDocuments()
    {
        return documentsView;
    }

    public void addPhrases(String... phrases)
    {
        labelCache = null;
        for (String phrase : phrases)
        {
            this.phrases.add(phrase);
        }
    }

    public void addPhrases(Iterable<String> phrases)
    {
        labelCache = null;
        for (String phrase : phrases)
        {
            this.phrases.add(phrase);
        }
    }

    public void addDocuments(Document... documents)
    {
        for (Document document : documents)
        {
            this.documents.add(document);
        }
        actualSizeCache = -1;
    }

    public void addDocuments(Iterable<Document> documents)
    {
        for (Document document : documents)
        {
            this.documents.add(document);
        }
        actualSizeCache = -1;
    }

    public void addSubclusters(Cluster... clusters)
    {
        for (Cluster cluster : clusters)
        {
            this.subclusters.add(cluster);
        }
        actualSizeCache = -1;
    }

    public void addSubclusters(Iterable<Cluster> clusters)
    {
        for (Cluster cluster : clusters)
        {
            this.subclusters.add(cluster);
        }
        actualSizeCache = -1;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key)
    {
        return (T) attributes.get(key);
    }

    public <T> void setAttribute(String key, T value)
    {
        attributes.put(key, value);
    }

    /**
     * Returns the size of the cluster calculated as the number of unique documents it
     * contains, including its subclusters.
     */
    public int size()
    {
        if (actualSizeCache == -1)
        {
            actualSizeCache = calculateSize(this, new HashSet<Document>());
        }

        return actualSizeCache;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || !(obj instanceof Cluster))
        {
            return false;
        }

        Cluster other = (Cluster) obj;

        return phrases.equals(other.phrases) && documents.equals(other.documents)
            && subclusters.equals(other.subclusters)
            && attributes.equals(other.attributes);
    }

    private int calculateSize(Cluster cluster, Set<Document> docs)
    {
        if (cluster == null)
        {
            return docs.size();
        }

        docs.addAll(cluster.getDocuments());
        List<Cluster> subclusters = cluster.getSubclusters();

        for (Cluster subcluster : subclusters)
        {
            calculateSize(subcluster, docs);
        }

        return docs.size();
    }

    public static final Comparator<Cluster> BY_SIZE_COMPARATOR = Comparators
        .nullLeastOrder(Comparators.fromFunction(new Function<Cluster, Integer>()
        {
            @Override
            public Integer apply(Cluster cluster)
            {
                return cluster.size();
            }
        }));

    public static final Comparator<Cluster> BY_LABEL_COMPARATOR = Comparators
        .nullLeastOrder(Comparators.fromFunction(new Function<Cluster, String>()
        {
            @Override
            public String apply(Cluster cluster)
            {
                return cluster.getLabel();
            }
        }));

    /**
     * Compares clusters first by their size as returned by {@link #size()} and labels as
     * returned by {@link #getLabel()}. Please note that cluster with a larger number of
     * documents is <b>smaller</b> according to this comparator, so that it ends up
     * towards the beginning of the list beind sorted. In case of equal sizes, natural
     * order of the labels decides.
     */
    public static final Comparator<Cluster> BY_REVERSED_SIZE_AND_LABEL_COMPARATOR = Comparators
        .compound(Collections.reverseOrder(BY_SIZE_COMPARATOR), BY_LABEL_COMPARATOR);
}
