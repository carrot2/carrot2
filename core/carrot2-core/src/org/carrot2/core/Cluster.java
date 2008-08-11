package org.carrot2.core;

import java.util.*;

import org.carrot2.util.StringUtils;
import org.carrot2.util.simplexml.TypeStringValuePair;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

import com.google.common.base.Function;
import com.google.common.collect.Comparators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A cluster (group) of {@link Document}s. Each cluster has a human-readable label
 * consisting of one or more phrases, a list of documents it contains and a list of its
 * subclusters. Optionally, additional attributes can be associated with a cluster, e.g.
 * {@link #OTHER_TOPICS}. This class is <strong>not</strong> thread-safe.
 */
@Root(name = "group", strict = false)
public final class Cluster
{
    /**
     * Indicates that the cluster is an "(Other Topics)" cluster. Such a cluster contains
     * documents that remain unclustered at given level of cluster hierarchy.
     * <p>
     * Type of this attribute is {@link Boolean}.
     * </p>
     * 
     * @see #setAttribute(String, Object)
     * @see #getAttribute(String)
     */
    public static final String OTHER_TOPICS = "other-topics";

    /**
     * Score of this cluster that indicates the clustering algorithm's beliefs on the
     * quality of this cluster. The exact semantics of the score varies across algorithms.
     * <p>
     * Type of this attribute is {@link Double}.
     * </p>
     * 
     * @see #setAttribute(String, Object)
     * @see #getAttribute(String)
     */
    public static final String SCORE = "score";

    /**
     * @see #getId()
     */
    @Attribute(required = false)
    Integer id;

    /** Phrases describing this cluster. */
    @ElementList(required = false, name = "title", entry = "phrase")
    private ArrayList<String> phrases = new ArrayList<String>();

    /** A read-only list of phrases exposed in {@link #getPhrases()}. */
    private List<String> phrasesView = Collections.unmodifiableList(phrases);

    /** Subclusters of this cluster. */
    @ElementList(required = false, inline = true)
    private ArrayList<Cluster> subclusters = new ArrayList<Cluster>();

    /** A read-only list of subclusters exposed in {@link #getSubclusters()}. */
    private List<Cluster> subclustersView = Collections.unmodifiableList(subclusters);

    /** Documents contained in this cluster. */
    private final ArrayList<Document> documents = new ArrayList<Document>();

    /** A read-only list of this cluster's document exposed in {@link #getDocuments()}. */
    private final List<Document> documentsView = Collections.unmodifiableList(documents);

    /** Attributes of this cluster. */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /** A Read-only view of the attributes of this cluster. */
    private Map<String, Object> attributesView = Collections.unmodifiableMap(attributes);

    /** Cached concatenated label */
    private String labelCache = null;

    /** Cached list of documents from this cluster and subclusters */
    private List<Document> allDocuments;

    /** Score of this cluster for serialization/ deserialization purposes. */
    @Attribute(required = false)
    private Double score;

    /** Attributes of this cluster for serialization/ deserialization purposes. */
    @ElementMap(name = "attributes", entry = "attribute", key = "key", value = "value", inline = true, attribute = true, required = false)
    private Map<String, TypeStringValuePair> otherAttributesAsStrings = new HashMap<String, TypeStringValuePair>();

    /** The actual size of this cluster, for serialization purposes only */
    @SuppressWarnings("unused")
    @Attribute(required = false)
    private int size;

    /**
     * List of document ids used for serialization/ deserialization purposes.
     */
    @ElementList(required = false, inline = true)
    List<DocumentRefid> documentIds;

    /**
     * A helper class for serialization/ deserialization of documents with refids.
     */
    @Root(name = "document")
    static class DocumentRefid
    {
        @Attribute
        Integer refid;

        DocumentRefid()
        {
        }

        DocumentRefid(Integer refid)
        {
            this.refid = refid;
        }
    }

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
     * Returns all documents in this cluster ordered according to the provided comparator.
     * See {@link Document} for common comparators, e.g. {@link Document#BY_ID_COMPARATOR}
     * .
     */
    public List<Document> getAllDocuments(Comparator<Document> comparator)
    {
        final List<Document> sortedDocuments = Lists.newArrayList(getAllDocuments());
        Collections.sort(sortedDocuments, comparator);
        return sortedDocuments;
    }

    /**
     * A recursive routine for collecting unique documents from this cluster and
     * subclusters.
     */
    private static Set<Document> collectAllDocuments(Cluster cluster, Set<Document> docs)
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
     * Internal identifier of this cluster within the {@link ProcessingResult}. This
     * identifier is assigned dynamically after clusters are passed to
     * {@link ProcessingResult}.
     * 
     * @see ProcessingResult
     */
    public Integer getId()
    {
        return id;
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
     * Compares clusters by score as returned by {@link #SCORE}. Clusters with larger
     * score are larger.
     */
    public static final Comparator<Cluster> BY_SCORE_COMPARATOR = Comparators
        .nullLeastOrder(Comparators.fromFunction(new Function<Cluster, Double>()
        {
            public Double apply(Cluster cluster)
            {
                return cluster.getAttribute(SCORE);
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

    /**
     * Compares clusters first by their size as returned by {@link #SCORE} and labels as
     * returned by {@link #getLabel()}. Please note that cluster with a larger score is
     * <b>smaller</b> according to this comparator, so that it ends up towards the
     * beginning of the list being sorted. In case of equal scores, natural order of the
     * labels decides.
     */
    public static final Comparator<Cluster> BY_REVERSED_SCORE_AND_LABEL_COMPARATOR = Comparators
        .compound(Collections.reverseOrder(BY_SCORE_COMPARATOR), BY_LABEL_COMPARATOR);

    /**
     * Assigns sequential identifiers to the provided <code>clusters</code> (and their
     * sub-clusters). If a cluster already has an identifier, the identifier will not be
     * changed.
     * 
     * @param clusters Clusters to assign identifiers to.
     * @throws IllegalArgumentException if the provided clusters contain non-unique
     *             identifiers
     */
    public static void assignClusterIds(Collection<Cluster> clusters)
    {
        final ArrayList<Cluster> flattened = Lists.newArrayListWithCapacity(clusters
            .size());

        flatten(flattened, clusters);

        synchronized (clusters)
        {
            final HashSet<Integer> ids = Sets.newHashSet();

            // First, find the start value for the id and check uniqueness of the ids
            // already provided.
            int maxId = Integer.MIN_VALUE;
            for (final Cluster cluster : flattened)
            {
                if (cluster.id != null)
                {
                    if (!ids.add(cluster.id))
                    {
                        throw new IllegalArgumentException(
                            "Non-unique cluster id found: " + cluster.id);
                    }
                    maxId = Math.max(maxId, cluster.id);
                }
            }

            // We'd rather start with 0
            maxId = Math.max(maxId, -1);

            // Assign missing ids
            for (final Cluster c : flattened)
            {
                if (c.id == null)
                {
                    c.id = ++maxId;
                }
            }
        }
    }

    /*
     * Recursive descent into subclusters.
     */
    private static void flatten(ArrayList<Cluster> flattened, Collection<Cluster> clusters)
    {
        for (Cluster c : clusters)
        {
            flattened.add(c);
            final List<Cluster> subclusters = c.getSubclusters();
            if (!subclusters.isEmpty())
            {
                flatten(flattened, subclusters);
            }
        }
    }

    /**
     * Locate the first cluster that has id equal to <code>id</code>. The search includes
     * all the clusters in the input and their sub-clusters. The first cluster with
     * matching identifier is returned or <code>null</code> if no such cluster could be
     * found.
     */
    public static Cluster find(int id, Collection<Cluster> clusters)
    {
        for (Cluster c : clusters)
        {
            if (c != null)
            {
                if (c.id != null && c.id == id)
                {
                    return c;
                }

                if (!c.getSubclusters().isEmpty())
                {
                    final Cluster sub = find(id, c.getSubclusters());
                    if (sub != null)
                    {
                        return sub;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Builds an "Other Topics" cluster that groups those documents from
     * <code>allDocument</code> that were not referenced in any cluster in
     * <code>clusters</code>.
     * 
     * @param allDocuments all documents to check against
     * @param clusters list of clusters with assigned documents
     * @return the "Other Topics" cluster
     */
    public static Cluster buildOtherTopics(List<Document> allDocuments,
        List<Cluster> clusters)
    {
        return buildOtherTopics(allDocuments, clusters, "Other Topics");
    }

    /**
     * Builds an "Other Topics" cluster that groups those documents from
     * <code>allDocument</code> that were not referenced in any cluster in
     * <code>clusters</code>.
     * 
     * @param allDocuments all documents to check against
     * @param clusters list of clusters with assigned documents
     * @param label label for the "Other Topics" group
     * @return the "Other Topics" cluster
     */
    public static Cluster buildOtherTopics(List<Document> allDocuments,
        List<Cluster> clusters, String label)
    {
        final Set<Document> unclusteredDocuments = Sets.newLinkedHashSet(allDocuments);
        final Set<Document> assignedDocuments = Sets.newHashSet();

        for (Cluster cluster : clusters)
        {
            collectAllDocuments(cluster, assignedDocuments);
        }

        unclusteredDocuments.removeAll(assignedDocuments);

        final Cluster otherTopics = new Cluster(label);
        otherTopics.addDocuments(unclusteredDocuments);
        otherTopics.setAttribute(Cluster.OTHER_TOPICS, Boolean.TRUE);

        return otherTopics;
    }

    /**
     * If there are unclustered documents, appends the "Other Topics" group to the
     * <code>clusters</code>.
     * 
     * @see #buildOtherTopics(List, List)
     */
    public static void appendOtherTopics(List<Document> allDocuments,
        List<Cluster> clusters)
    {
        appendOtherTopics(allDocuments, clusters, "Other Topics");
    }

    /**
     * If there are unclustered documents, appends the "Other Topics" group to the
     * <code>clusters</code>.
     * 
     * @see #buildOtherTopics(List, List, String)
     */
    public static void appendOtherTopics(List<Document> allDocuments,
        List<Cluster> clusters, String label)
    {
        final Cluster otherTopics = buildOtherTopics(allDocuments, clusters, label);
        if (!otherTopics.getDocuments().isEmpty())
        {
            clusters.add(otherTopics);
        }
    }

    @Persist
    @SuppressWarnings("unused")
    private void beforeSerialization()
    {
        documentIds = Lists.newArrayListWithCapacity(documents.size());
        for (Document document : documents)
        {
            documentIds.add(new DocumentRefid(document.getId()));
        }

        score = getAttribute(SCORE);
        size = size();

        // Remove score from attributes for serialization
        otherAttributesAsStrings = TypeStringValuePair.toTypeStringValuePairs(attributes);
        otherAttributesAsStrings.remove(SCORE);
        if (otherAttributesAsStrings.isEmpty())
        {
            otherAttributesAsStrings = null;
        }
    }

    @Commit
    @SuppressWarnings("unused")
    private void afterDeserialization() throws Exception
    {
        if (otherAttributesAsStrings != null)
        {
            attributes.putAll(otherAttributesAsStrings);
            attributes = TypeStringValuePair.fromTypeStringValuePairs(
                new HashMap<String, Object>(), otherAttributesAsStrings);
        }

        if (score != null)
        {
            attributes.put(SCORE, score);
        }

        attributesView = Collections.unmodifiableMap(attributes);
        phrasesView = Collections.unmodifiableList(phrases);
        subclustersView = Collections.unmodifiableList(subclusters);
        // Documents will be restored on the ProcessingResult level
    }
}
