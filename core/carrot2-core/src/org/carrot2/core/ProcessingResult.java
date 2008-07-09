package org.carrot2.core;

import java.io.*;
import java.util.*;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.simplexml.TypeStringValuePair;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.*;

import com.google.common.collect.Lists;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents (
 * {@link #getDocuments()})) and the created clusters ({@link #getClusters()}).
 */
@Root(name = "searchresult", strict = false)
public final class ProcessingResult
{
    /** Attributes collected after processing */
    private Map<String, Object> attributes;

    /** Read-only view of attributes exposed in {@link #getAttributes()} */
    private Map<String, Object> attributesView;

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

    /** Attributes of this result for serialization/ deserialization purposes. */
    @ElementMap(name = "attributes", entry = "attribute", key = "key", value = "value", 
        inline = true, attribute = true, required = false)
    private Map<String, TypeStringValuePair> otherAttributesAsStrings = 
        new HashMap<String, TypeStringValuePair>();

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
            Document.assignDocumentIds(documents);
            attributes.put(AttributeNames.DOCUMENTS, Collections
                .unmodifiableList(documents));
        }

        // Replace a modifiable collection of clusters with an unmodifiable one
        final List<Cluster> clusters = (List<Cluster>) attributes
            .get(AttributeNames.CLUSTERS);
        if (clusters != null)
        {
            Cluster.assignClusterIds(clusters);
            attributes.put(AttributeNames.CLUSTERS, Collections
                .unmodifiableList(clusters));
        }

        // Store a reference to attributes as an unmodifiable map
        this.attributesView = Collections.unmodifiableMap(attributes);

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
     * @return documents that have been processed or <code>null</code> if no documents are
     *         present in the result.
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
     * @return clusters created during processing or <code>null</code> if no clusters were
     *         present in the result.
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

        otherAttributesAsStrings = TypeStringValuePair.toTypeStringValuePairs(attributes);
        otherAttributesAsStrings.remove(AttributeNames.QUERY);
        otherAttributesAsStrings.remove(AttributeNames.CLUSTERS);
        otherAttributesAsStrings.remove(AttributeNames.DOCUMENTS);
        if (otherAttributesAsStrings.isEmpty())
        {
            otherAttributesAsStrings = null;
        }
    }

    /**
     * Transfers document and cluster lists to the attributes map after deserialization.
     */
    @Commit
    @SuppressWarnings("unused")
    private void afterDeserialization() throws Exception
    {
        attributes = TypeStringValuePair.fromTypeStringValuePairs(
            new HashMap<String, Object>(), otherAttributesAsStrings);
        attributesView = Collections.unmodifiableMap(attributes);

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
     * Serializes this {@link ProcessingResult} to an XML file.
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
     * Serializes this {@link ProcessingResult} to an XML writer. This method is not
     * thread-safe, external synchronization must be applied if needed.
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
     * Deserializes a {@link ProcessingResult} from an XML character stream.
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
    
    /**
     * Deserializes a {@link ProcessingResult} from an XML byte stream.
     * 
     * @param stream the stream to deserialize a {@link ProcessingResult} from. The stream
     *            will <strong>not</strong> be closed.
     * @return deserialized {@link ProcessingResult}
     * @throws Exception is case of any problems with deserialization
     */
    public static ProcessingResult deserialize(InputStream stream) throws Exception
    {
        return new Persister().read(ProcessingResult.class, stream);
    }
}