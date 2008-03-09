package org.carrot2.core;

import java.io.*;
import java.util.*;

import org.carrot2.core.attribute.AttributeNames;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.*;

import com.google.common.collect.Lists;

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
    @ElementList(inline = true)
    private List<Document> documents;

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
        final Collection<Document> documents = (Collection<Document>) attributes
            .get(AttributeNames.DOCUMENTS);
        if (documents != null)
        {
            attributes.put(AttributeNames.DOCUMENTS, Collections
                .unmodifiableCollection(documents));
        }

        // Replace a modifiable collection of clusters with an unmodifiable one
        final Collection<Cluster> clusters = (Collection<Cluster>) attributes
            .get(AttributeNames.CLUSTERS);
        if (clusters != null)
        {
            attributes.put(AttributeNames.CLUSTERS, Collections
                .unmodifiableCollection(clusters));
        }

        // Store a reference to attributes as an unmodifiable map
        this.attributesView = Collections.unmodifiableMap(attributes);

        assignDocumentIds();
    }

    /**
     * Assigns sequential identifiers to documents.
     */
    private void assignDocumentIds()
    {
        final Collection<Document> documents = getDocuments();
        if (documents != null)
        {
            int id = 0;
            for (final Document document : documents)
            {
                document.id = id++;
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
    public Collection<Document> getDocuments()
    {
        return (Collection<Document>) attributes.get(AttributeNames.DOCUMENTS);
    }

    /*
     * TODO: Returning a list of clusters instead of a (possibly atrificial) cluster with
     * subclusters adds a little complexity to recursive methods operating on clusters (a
     * natural entry point is a method taking one cluster and acting on subclusters
     * recursively). If we have to start with a list of clusters, we have to handle this
     * special case separately...
     */

    /**
     * Returns the clusters that have been created during processing. The returned
     * collection is unmodifiable.
     * 
     * @return clusters created during processing or <code>null</code> if no clusters
     *         were present in the result.
     */
    @SuppressWarnings("unchecked")
    public Collection<Cluster> getClusters()
    {
        return (Collection<Cluster>) attributes.get(AttributeNames.CLUSTERS);
    }

    /**
     * Extracts document and cluster lists before serialization.
     */
    @Persist
    private void beforeSerialization()
    {
        query = (String) attributes.get(AttributeNames.QUERY);
        documents = Lists.newArrayList(getDocuments());
    }

    /**
     * Transfers document and cluster lists to the attributes map after deserialization.
     */
    @Commit
    private void afterDeserialization()
    {
        attributes.put(AttributeNames.QUERY, query);
        attributes.put(AttributeNames.DOCUMENTS, documents);
    }

    /**
     * Serializes this {@link ProcessingResult} to an XML stream.
     * 
     * @param outputStream the stream to serialize this {@link ProcessingResult} to. The
     *            stream will <strong>not</strong> be closed.
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(OutputStream outputStream) throws Exception
    {
        new Persister().write(this, outputStream);
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
        new Persister().write(this, writer);
    }

    /**
     * Deserializes a {@link ProcessingResult} from an XML stream.
     * 
     * @param inputStream the stream to deserialize a {@link ProcessingResult} from. The
     *            stream will <strong>not</strong> be closed.
     * @return deserialized {@link ProcessingResult}
     * @throws Exception is case of any problems with deserialization
     */
    public static ProcessingResult deserialize(InputStream inputStream) throws Exception
    {
        return new Persister().read(ProcessingResult.class, inputStream);
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