package org.carrot2.core;

import java.util.*;

import org.carrot2.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Comparators;

/**
 * A cluster (group) of {@link Document}s. Each cluster has a human-readable label
 * consisting of one or more phrases, a list of documents it contains and a list of its
 * subclusters. Optionally, additional attributes can be associated with a cluster, e.g.
 * {@link #OTHER_TOPICS}. This class is <strong>not</strong> thread-safe.
 */
public final class Cluster
{
    /**
     * Indicates that the cluster is an "(Other Topics)" cluster. Such a cluster contains
     * documents that remain unclustered at given level of cluster hierarchy.
     * 
     * @see #setAttribute(String, Object)
     * @see #getAttribute(String)
     */
    public static final String OTHER_TOPICS = "other-topics";

    /** Phrases describing this cluster. */
    private final ArrayList<String> phrases = new ArrayList<String>();

    /** A read-only list of phrases exposed in {@link #getPhrases()}. */
    private final List<String> phrasesView = Collections.unmodifiableList(phrases);

    /** Subclusters of this cluster. */
    private final ArrayList<Cluster> subclusters = new ArrayList<Cluster>();

    /** A read-only list of subclusters exposed in {@link #getSubclusters()}. */
    private final List<Cluster> subclustersView = Collections
        .unmodifiableList(subclusters);

    /** Documents contained in this cluster. */
    private final ArrayList<Document> documents = new ArrayList<Document>();

    /** A read-only list of this cluster's document exposed in {@link #getDocuments()}. */
    private final List<Document> documentsView = Collections.unmodifiableList(documents);

    /** Attributes of this cluster. */
    private final HashMap<String, Object> attributes = new HashMap<String, Object>();

    /** A Read-only view of the attributes of this cluster. */
    private final Map<String, Object> attributesView = Collections
        .unmodifiableMap(attributes);

    /** Cached concatenated label */
    private String labelCache = null;

    /** Cached list of documents from this cluster and subclusters */
    private List<Document> allDocuments;

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
     * Returns all documents contained in this cluster and (recursively) all documents
     * from this cluster's subclusters. The returned list contains unique documents, i.e.
     * if a document is attached to multiple subclusters if this cluster, the document
     * will appear only once on the list. The documents are enumerated in breadth first
     * order, i.e. first come documents returned by {@link #getDocuments()} and then
     * documents from subclusters.
     * 
     * @return all documents from this cluster and its subclusters
     */
    public List<Document> getAllDocuments()
    {
        if (allDocuments == null)
        {
            allDocuments = new ArrayList<Document>(collectAllDocuments(this,
                new LinkedHashSet<Document>()));
        }

        return allDocuments;
    }

    /**
     * A recursive routine for collecting unique documents from this cluster and
     * subclusters.
     */
    private Set<Document> collectAllDocuments(Cluster cluster, Set<Document> docs)
    {
        if (cluster == null)
        {
            return docs;
        }

        docs.addAll(cluster.getDocuments());

        final List<Cluster> subclusters = cluster.getSubclusters();
        for (final Cluster subcluster : subclusters)
        {
            collectAllDocuments(subcluster, docs);
        }

        return docs;
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
        allDocuments = null;

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
        allDocuments = null;

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
        allDocuments = null;

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
        allDocuments = null;

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
     * Returns all attributes of this cluster. The returned map is unmodifiable.
     * 
     * @return all attributes of this cluster
     */
    public Map<String, Object> getAttributes()
    {
        return attributesView;
    }

    /**
     * Returns the size of the cluster calculated as the number of unique documents it
     * contains, including its subclusters.
     * 
     * @return size of the cluster
     */
    public int size()
    {
        return getAllDocuments().size();
    }

    /**
     * Compares clusters by size as returned by {@link #size()}. Clusters with more
     * documents are larger.
     */
    public static final Comparator<Cluster> BY_SIZE_COMPARATOR = Comparators
        .nullLeastOrder(Comparators.fromFunction(new Function<Cluster, Integer>()
        {
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
    public static final Comparator<Cluster> BY_REVERSED_SIZE_AND_LABEL_COMPARATOR = Comparators
        .compound(Collections.reverseOrder(BY_SIZE_COMPARATOR), BY_LABEL_COMPARATOR);
}
