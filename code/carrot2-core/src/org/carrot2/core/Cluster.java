package org.carrot2.core;

import java.util.*;

import org.carrot2.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Comparators;

/**
 * A cluster (group) of {@link Document}s. Each cluster has a human-readable label
 * consisting of one or more phrases, a list of documents it contains and a list of its
 * subclusters. Optionally, additional attributes can be associated with a cluster, e.g.
 * {@link #OTHER_TOPICS}.
 */
public final class Cluster
{
    /**
     * Indicates that the cluster is an "(Other Topics)" cluster. Such a cluster contains
     * documents that remain unclustered at given level of cluster hierarchy.
     */
    public static final String OTHER_TOPICS = "other-topics";

    /** Phrases describing this cluster. */
    private final ArrayList<String> phrases = new ArrayList<String>();

    /** A read-only list of phrases exposed in {@link #getPhrases()}. */
    private final List<String> phrasesView = Collections.unmodifiableList(phrases);

    /** Subclusters of this cluster. */
    private final ArrayList<Cluster> subclusters = new ArrayList<Cluster>();

    /** A read-only list of subclusters exposed in {@link #getSubclusters()}. */
    private final List<Cluster> subclustersView = Collections.unmodifiableList(subclusters);

    /** Documents contained in this cluster. */
    private final ArrayList<Document> documents = new ArrayList<Document>();

    /** A read-only list of this cluster's document exposed in {@link #getDocuments()}. */
    private final List<Document> documentsView = Collections.unmodifiableList(documents);

    /** Attributes of this cluster. */
    private final HashMap<String, Object> attributes = new HashMap<String, Object>();

    /** Cached concatenated label */
    private String labelCache = null;

    /** Cached actual cluster size */
    private int actualSizeCache = -1;

    /**
     * Creates a {@link Cluster} with an empty label, no documents and no subclusters.
     */
    public Cluster()
    {
    }

    /**
     * Creates a {@link Cluster} with the provided <code>phrase</code> to be used as the
     * cluster's label and <code>documents</code> contained in the cluster.
     *
     * @param phrase the phrase to form the cluster's label
     * @param documents documents contained in the cluster
     */
    public Cluster(String phrase, Document... documents)
    {
        addPhrases(phrase);
        addDocuments(documents);
    }

    /**
     * Formats this cluster's label. If there is more than one phrase describing this
     * cluster, phrases will be separated by a comma followed by a space, e.g. "Phrase
     * one, Phrase two". To format multi-phrase label in a different way, use
     * {@link #getPhrases()}.
     *
     * @return formatted label of this cluster
     */
    public String getLabel()
    {
        if (labelCache == null)
        {
            labelCache = StringUtils.toString(phrases, ", ");
        }
        return labelCache;
    }

    /**
     * Returns all phrases describing this cluster. The returned list is unmodifiable.
     *
     * @return phrases describing this cluster
     */
    public List<String> getPhrases()
    {
        return phrasesView;
    }

    /**
     * Returns all subclusters of this cluster. The returned list is unmodifiable.
     *
     * @return subclusters of this cluster
     */
    public List<Cluster> getSubclusters()
    {
        return subclustersView;
    }

    /**
     * Returns all documents contained in this cluster. The returned list is unmodifiable.
     *
     * @return documents contained in this cluster
     */
    public List<Document> getDocuments()
    {
        return documentsView;
    }

    /**
     * Adds phrases to the description of this cluster.
     *
     * @param phrases to be added to the description of this cluster
     * @return this cluster for convenience
     */
    public Cluster addPhrases(String... phrases)
    {
        labelCache = null;
        for (final String phrase : phrases)
        {
            this.phrases.add(phrase);
        }

        return this;
    }

    /**
     * Adds phrases to the description of this cluster.
     *
     * @param phrases to be added to the description of this cluster
     * @return this cluster for convenience
     */
    public Cluster addPhrases(Iterable<String> phrases)
    {
        labelCache = null;
        for (final String phrase : phrases)
        {
            this.phrases.add(phrase);
        }

        return this;
    }

    /**
     * Adds document to this cluster.
     *
     * @param documents to be added to this cluster
     * @return this cluster for convenience
     */
    public Cluster addDocuments(Document... documents)
    {
        for (final Document document : documents)
        {
            this.documents.add(document);
        }
        actualSizeCache = -1;

        return this;
    }

    /**
     * Adds document to this cluster.
     *
     * @param documents to be added to this cluster
     * @return this cluster for convenience
     */
    public Cluster addDocuments(Iterable<Document> documents)
    {
        for (final Document document : documents)
        {
            this.documents.add(document);
        }
        actualSizeCache = -1;

        return this;
    }

    /**
     * Adds subclusters to this cluster
     *
     * @param subclusters to be added to this cluster
     * @return this cluster for convenience
     */
    public Cluster addSubclusters(Cluster... subclusters)
    {
        for (final Cluster cluster : subclusters)
        {
            this.subclusters.add(cluster);
        }
        actualSizeCache = -1;

        return this;
    }

    /**
     * Adds subclusters to this cluster
     *
     * @param clusters to be added to this cluster
     * @return this cluster for convenience
     */
    public Cluster addSubclusters(Iterable<Cluster> clusters)
    {
        for (final Cluster cluster : clusters)
        {
            this.subclusters.add(cluster);
        }
        actualSizeCache = -1;

        return this;
    }

    /**
     * Returns the attribute associated with this cluster under the provided
     * <code>key</code>. If there is no attribute under the provided <code>key</code>,
     * <code>null</code> will be returned.
     *
     * @param key of the attribute
     * @return attribute value of <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key)
    {
        return (T) attributes.get(key);
    }

    /**
     * Associates an attribute with this cluster.
     *
     * @param key for the attribute
     * @param value for the attribute
     * @return this cluster for convenience
     */
    public <T> Cluster setAttribute(String key, T value)
    {
        attributes.put(key, value);
        return this;
    }

    /**
     * Returns the size of the cluster calculated as the number of unique documents it
     * contains, including its subclusters.
     *
     * @return size of the cluster
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

        final Cluster other = (Cluster) obj;

        // TODO: List comparisons work on SUN's JRE, but they are often not implemented
        // properly on other JVMs (i.e. they are buggy in Microsoft's JVM for example).
        // It is simply and safe to have our own array-comparison utility here.
        return subclusters.equals(other.subclusters)
            && phrases.equals(other.phrases)
            && documents.equals(other.documents)
            && attributes.equals(other.attributes);
    }
    

    @Override
    public int hashCode()
    {
        return 0;
    }

    /**
     * A recursive routine for calculating the size of the cluster.
     */
    private int calculateSize(Cluster cluster, Set<Document> docs)
    {
        if (cluster == null)
        {
            return docs.size();
        }

        docs.addAll(cluster.getDocuments());

        final List<Cluster> subclusters = cluster.getSubclusters();
        for (final Cluster subcluster : subclusters)
        {
            calculateSize(subcluster, docs);
        }

        return docs.size();
    }

    /**
     * Compares clusters by size as returned by {@link #size()}. Clusters with more
     * documents are larger.
     */
    public static final Comparator<Cluster> BY_SIZE_COMPARATOR = Comparators
        .nullLeastOrder(Comparators.fromFunction(new Function<Cluster, Integer>()
        {
            @Override
            public Integer apply(Cluster cluster)
            {
                return cluster.size();
            }
        }));

    /**
     * Compares clusters by the natural order of their labels as returned by
     * {@link #getLabel()}.
     */
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
     * towards the beginning of the list being sorted. In case of equal sizes, natural
     * order of the labels decides.
     */
    public static final Comparator<Cluster> BY_REVERSED_SIZE_AND_LABEL_COMPARATOR =
        Comparators.compound(
            Collections.reverseOrder(BY_SIZE_COMPARATOR), BY_LABEL_COMPARATOR);
}
