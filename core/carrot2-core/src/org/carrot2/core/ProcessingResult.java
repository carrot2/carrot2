package org.carrot2.core;

import java.io.*;
import java.util.*;

import org.carrot2.core.attribute.AttributeNames;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents ({@link #getDocuments()}))
 * and the created clusters ({@link #getClusters()}).
 */
@Root(name = "searchresult", strict = false)
public final class ProcessingResult
{
    /** Attributes collected after processing */
    private final Map<String, Object> attributes;

    /** Read-only view of attributes exposed in {@link #getAttributes()} */
    private final Map<String, Object> attributesView;

    /**
     * Query field used during serialization/ deserialization, see
     * {@link #afterDeserialization()} and {@link #beforeSerialization()}
     */
    @Element(required = false)
    private String query;

    /**
     * Documents field used during serialization/ deserialization, see
     * {@link #afterDeserialization()} and {@link #beforeSerialization()}
     */
    @ElementList(inline = true, required = false)
    private List<Document> documents;

    /**
     * Clusters field used during serialization/ deserialization, see
     * {@link #afterDeserialization()} and {@link #beforeSerialization()}
     */
    @ElementList(inline = true, required = false)
    private List<Cluster> clusters;

    /**
     * Parameterless constructor required for XML serialization/ deserialization.
     */
    ProcessingResult()
    {
        this(new HashMap<String, Object>());
    }

    /**
     * Creates a {@link ProcessingResult} with the provided <code>attributes</code>.
     * Assigns unique document identifiers if documents are present in the
     * <code>attributes</code> map (under the key {@link AttributeNames#DOCUMENTS}).
     */
    @SuppressWarnings("unchecked")
    ProcessingResult(Map<String, Object> attributes)
    {
        this.attributes = attributes;

        // Replace a modifiable collection of documents with an unmodifiable one
        final List<Document> documents = (List<Document>) attributes
            .get(AttributeNames.DOCUMENTS);
        if (documents != null)
        {
            assignDocumentIds(documents);
            attributes.put(AttributeNames.DOCUMENTS, Collections
                .unmodifiableList(documents));
        }

        // Replace a modifiable collection of clusters with an unmodifiable one
        final List<Cluster> clusters = (List<Cluster>) attributes
            .get(AttributeNames.CLUSTERS);
        if (clusters != null)
        {
            attributes.put(AttributeNames.CLUSTERS, Collections
                .unmodifiableList(clusters));
        }

        // Store a reference to attributes as an unmodifiable map
        this.attributesView = Collections.unmodifiableMap(attributes);

    }

    /**
     * Assigns sequential identifiers to documents.
     */
    private void assignDocumentIds(Collection<Document> documents)
    {
        // We may get concurrent calls referring to the same documents
        // in the same list, so we need to synchronize here.
        synchronized (documents)
        {
            final HashSet<Integer> ids = Sets.newHashSet();

            // First, find the start value for the id and check uniqueness of the ids
            // already provided.
            int maxId = Integer.MIN_VALUE;
            for (final Document document : documents)
            {
                if (document.id != null)
                {
                    if (!ids.add(document.id))
                    {
                        throw new RuntimeException("Non-unique document id found: "
                            + document.id);
                    }
                    maxId = Math.max(maxId, document.id);
                }
            }

            // We'd rather start with 0
            maxId = Math.max(maxId, -1);

            // Assign missing ids
            for (final Document document : documents)
            {
                if (document.id == null)
                {
                    document.id = ++maxId;
                }
            }
        }
    }

    /**
     * Returns attributes fed-in and collected during processing. The returned map is
     * unmodifiable.
     * 
     * @return attributes fed-in and collected during processing
     */
    public Map<String, Object> getAttributes()
    {
        return attributesView;
    }

    /**
     * Returns the documents that have been processed. The returned collection is
     * unmodifiable.
     * 
     * @return documents that have been processed or <code>null</code> if no documents
     *         are present in the result.
     */
    @SuppressWarnings("unchecked")
    public List<Document> getDocuments()
    {
        return (List<Document>) attributes.get(AttributeNames.DOCUMENTS);
    }

    /*
     * TODO: Returning a list of clusters instead of a (possibly artificial) cluster with
     * subclusters adds a little complexity to recursive methods operating on clusters (a
     * natural entry point is a method taking one cluster and acting on subclusters
     * recursively). If we have to start with a list of clusters, we have to handle this
     * special case separately...
     */

    /**
     * Returns the clusters that have been created during processing. The returned list is
     * unmodifiable.
     * 
     * @return clusters created during processing or <code>null</code> if no clusters
     *         were present in the result.
     */
    @SuppressWarnings("unchecked")
    public List<Cluster> getClusters()
    {
        return (List<Cluster>) attributes.get(AttributeNames.CLUSTERS);
    }

    /**
     * Extracts document and cluster lists before serialization.
     */
    @Persist
    @SuppressWarnings("unused")
    private void beforeSerialization()
    {
        query = (String) attributes.get(AttributeNames.QUERY);
        if (getDocuments() != null)
        {
            documents = Lists.newArrayList(getDocuments());
        }
        if (getClusters() != null)
        {
            clusters = Lists.newArrayList(getClusters());
        }
    }

    /**
     * Transfers document and cluster lists to the attributes map after deserialization.
     */
    @Commit
    @SuppressWarnings("unused")
    private void afterDeserialization()
    {
        attributes.put(AttributeNames.QUERY, query);
        attributes.put(AttributeNames.DOCUMENTS, documents);
        attributes.put(AttributeNames.CLUSTERS, clusters);

        // Convert document ids to the actual references
        if (clusters != null && documents != null)
        {
            for (Cluster cluster : clusters)
            {
                documentIdToReference(cluster, documents);
            }
        }
    }

    /**
     * Replace document refids with the actual references upon deserialization.
     */
    private void documentIdToReference(Cluster cluster, List<Document> documents)
    {
        if (cluster.documentIds != null)
        {
            for (Cluster.DocumentRefid documentRefid : cluster.documentIds)
            {
                cluster.addDocuments(documents.get(documentRefid.refid));
            }
        }

        for (Cluster subcluster : cluster.getSubclusters())
        {
            documentIdToReference(subcluster, documents);
        }
    }

    /**
     * Serializes this {@link ProcessingResult} to an XML writer.
     * 
     * @param writer the writer to serialize this {@link ProcessingResult} to. The writer
     *            will <strong>not</strong> be closed.
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(Writer writer) throws Exception
    {
        serialize(writer, true, true);
    }

    /**
     * Serializes this {@link ProcessingResult} to an XML writer.
     * 
     * @param writer the writer to serialize this {@link ProcessingResult} to. The writer
     *            will <strong>not</strong> be closed.
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     *            Notice that when deserializing XML containing clusters but not
     *            documents, document references in {@link Cluster#getDocuments()} will
     *            not be restored.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(Writer writer, boolean saveDocuments, boolean saveClusters)
        throws Exception
    {
        final List<Document> documentsBackup = getDocuments();
        final List<Cluster> clustersBackup = getClusters();

        if (!saveDocuments)
        {
            attributes.remove(AttributeNames.DOCUMENTS);
        }

        if (!saveClusters)
        {
            attributes.remove(AttributeNames.CLUSTERS);
        }

        new Persister().write(this, writer);

        if (documentsBackup != null)
        {
            attributes.put(AttributeNames.DOCUMENTS, documentsBackup);
        }

        if (clustersBackup != null)
        {
            attributes.put(AttributeNames.CLUSTERS, clustersBackup);
        }
    }

    /**
     * Deserializes a {@link ProcessingResult} from an XML reader.
     * 
     * @param reader the reader to deserialize a {@link ProcessingResult} from. The reader
     *            will <strong>not</strong> be closed.
     * @return deserialized {@link ProcessingResult}
     * @throws Exception is case of any problems with deserialization
     */
    public static ProcessingResult deserialize(Reader reader) throws Exception
    {
        return new Persister().read(ProcessingResult.class, reader);
    }
}