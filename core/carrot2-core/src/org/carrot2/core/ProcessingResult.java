
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.MapUtils;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.carrot2.shaded.guava.common.collect.*;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents (
 * {@link #getDocuments()})) and the created clusters ({@link #getClusters()}).
 */
@Root(name = "searchresult", strict = false)
public final class ProcessingResult
{
    /** Attributes collected after processing */
    private Map<String, Object> attributes = Maps.newHashMap();

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
    @ElementMap(entry = "attribute", key = "key", attribute = true, inline = true, required = false)
    private HashMap<String, SimpleXmlWrapperValue> otherAttributesForSerialization;

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
            attributes.put(AttributeNames.DOCUMENTS,
                Collections.unmodifiableList(documents));
        }

        // Replace a modifiable collection of clusters with an unmodifiable one
        final List<Cluster> clusters = (List<Cluster>) attributes
            .get(AttributeNames.CLUSTERS);
        if (clusters != null)
        {
            Cluster.assignClusterIds(clusters);
            attributes.put(AttributeNames.CLUSTERS,
                Collections.unmodifiableList(clusters));
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
     * Returns a specific attribute of this result set. This method is equivalent to
     * calling {@link #getAttributes()} and then getting the required attribute from the
     * map.
     * 
     * @param key key of the attribute to return
     * @return value of the attribute
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key)
    {
        return (T) attributesView.get(key);
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
    private void beforeSerialization()
    {
        /*
         * See http://issues.carrot2.org/browse/CARROT-693; this monitor does not save us
         * in multi-threaded environment anyway. A better solution would be to prepare
         * this eagerly in the constructor, but we try to balance overhead and full
         * correctness here.
         */
        synchronized (this)
        {
            query = (String) attributes.get(AttributeNames.QUERY);

            if (getDocuments() != null)
            {
                documents = Lists.newArrayList(getDocuments());
            }
            else
            {
                documents = null;
            }

            if (getClusters() != null)
            {
                clusters = Lists.newArrayList(getClusters());
            }
            else
            {
                clusters = null;
            }

            otherAttributesForSerialization = MapUtils.asHashMap(SimpleXmlWrappers
                .wrap(attributes));
            otherAttributesForSerialization.remove(AttributeNames.QUERY);
            otherAttributesForSerialization.remove(AttributeNames.CLUSTERS);
            otherAttributesForSerialization.remove(AttributeNames.DOCUMENTS);
            if (otherAttributesForSerialization.isEmpty())
            {
                otherAttributesForSerialization = null;
            }
        }
    }

    /**
     * Transfers document and cluster lists to the attributes map after deserialization.
     */
    @Commit
    private void afterDeserialization() throws Exception
    {
        if (otherAttributesForSerialization != null)
        {
            attributes = SimpleXmlWrappers.unwrap(otherAttributesForSerialization);
        }

        attributesView = Collections.unmodifiableMap(attributes);

        attributes.put(AttributeNames.QUERY, query != null ? query.trim() : null);
        attributes.put(AttributeNames.DOCUMENTS, documents);
        attributes.put(AttributeNames.CLUSTERS, clusters);

        // Convert document ids to the actual references
        if (clusters != null && documents != null)
        {
            final Map<String, Document> documentsById = Maps.newHashMap();
            for (Document document : documents)
            {
                documentsById.put(document.getStringId(), document);
            }

            for (Cluster cluster : clusters)
            {
                documentIdToReference(cluster, documentsById);
            }
        }
    }

    /**
     * Replace document refids with the actual references upon deserialization.
     */
    private void documentIdToReference(Cluster cluster, Map<String, Document> documents)
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
     * Serializes this {@link ProcessingResult} to an XML string.
     */
    public String serialize()
    {
        try {
            StringWriter sw = new StringWriter();
            new Persister().write(this, sw);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Serializes this {@link ProcessingResult} to an XML stream. The output includes all
     * documents, clusters and other attributes.
     * <p>
     * This method is not thread-safe, external synchronization must be applied if needed.
     * </p>
     * 
     * @param stream the stream to serialize this {@link ProcessingResult} to. The stream
     *            will <strong>not</strong> be closed.
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(OutputStream stream) throws Exception
    {
        serialize(stream, true, true);
    }

    /**
     * Serializes this {@link ProcessingResult} to a byte stream. Documents and clusters
     * can be included or skipped in the output as requested. Other attributes are always
     * included.
     * <p>
     * This method is not thread-safe, external synchronization must be applied if needed.
     * </p>
     * 
     * @param stream the stream to serialize this {@link ProcessingResult} to. The stream
     *            will <strong>not</strong> be closed.
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     *            Notice that when deserializing XML containing clusters but not
     *            documents, document references in {@link Cluster#getDocuments()} will
     *            not be restored.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(OutputStream stream, boolean saveDocuments, boolean saveClusters)
        throws Exception
    {
        serialize(stream, saveDocuments, saveClusters, true);
    }

    /**
     * Serializes this {@link ProcessingResult} to a byte stream. Documents, clusters and
     * other attributes can be included or skipped in the output as requested.
     * <p>
     * This method is not thread-safe, external synchronization must be applied if needed.
     * </p>
     * 
     * @param stream the stream to serialize this {@link ProcessingResult} to. The stream
     *            will <strong>not</strong> be closed.
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     *            Notice that when deserializing XML containing clusters but not
     *            documents, document references in {@link Cluster#getDocuments()} will
     *            not be restored.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @param saveOtherAttributes if <code>false</code>, other attributes will not be
     *            serialized
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(OutputStream stream, boolean saveDocuments,
        boolean saveClusters, boolean saveOtherAttributes) throws Exception
    {
        final Map<String, Object> backupAttributes = attributes;

        attributes = prepareAttributesForSerialization(saveDocuments, saveClusters,
            saveOtherAttributes);

        new Persister().write(this, stream);

        attributes = backupAttributes;
    }
    
    /**
     * Deserialize from an input stream of characters.
     */
    public static ProcessingResult deserialize(CharSequence input) throws Exception
    {
        return new Persister().read(ProcessingResult.class, input.toString());
    }

    /**
     * Deserializes a {@link ProcessingResult} from an XML stream.
     * 
     * @param input the input XML stream to deserialize a {@link ProcessingResult} from.
     *            The stream will <strong>not</strong> be closed.
     * @return deserialized {@link ProcessingResult}
     * @throws Exception is case of any problems with deserialization
     */
    public static ProcessingResult deserialize(InputStream input) throws Exception
    {
        return new Persister().read(ProcessingResult.class, input);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>. The
     * output includes all documents, clusters and other attributes.
     * <p>
     * This method is not thread-safe, external synchronization must be applied if needed.
     * </p>
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @throws IOException in case of any problems with serialization
     */
    public void serializeJson(Writer writer) throws IOException
    {
        serializeJson(writer, null);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>. The
     * output includes all documents, clusters and other attributes.
     * <p>
     * This method is not thread-safe, external synchronization must be applied if needed.
     * </p>
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @param callback JavaScript function name in which to wrap the JSON response or
     *            <code>null</code>.
     * @throws IOException in case of any problems with serialization
     */
    public void serializeJson(Writer writer, String callback) throws IOException
    {
        serializeJson(writer, callback, true, true);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>.
     * Documents and clusters can be included or skipped in the output as requested. Other
     * attributes are always included.
     * <p>
     * This method is not thread-safe, external synchronization must be applied if needed.
     * </p>
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @param callback JavaScript function name in which to wrap the JSON response or
     *            <code>null</code>.
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @throws IOException in case of any problems with serialization
     */
    public void serializeJson(Writer writer, String callback, boolean saveDocuments,
        boolean saveClusters) throws IOException
    {
        serializeJson(writer, callback, false, saveDocuments, saveClusters);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>.
     * <p>
     * This method is not thread-safe, external synchronization must be applied if needed.
     * </p>
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @param callback JavaScript function name in which to wrap the JSON response or
     *            <code>null</code>.
     * @param indent if <code>true</code>, the output JSON will be pretty-printed
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @throws IOException in case of any problems with serialization
     */
    public void serializeJson(Writer writer, String callback, boolean indent,
        boolean saveDocuments, boolean saveClusters) throws IOException
    {
        serializeJson(writer, callback, indent, saveDocuments, saveClusters, true);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>.
     * Documents, clusters and other attributes can be included or skipped in the output
     * as requested.
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @param callback JavaScript function name in which to wrap the JSON response or
     *            <code>null</code>.
     * @param indent if <code>true</code>, the output JSON will be pretty-printed
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @param saveOtherAttributes if <code>false</code>, other attributes will not be
     *            serialized
     * @throws IOException in case of any problems with serialization
     */
    public void serializeJson(Writer writer, String callback, boolean indent,
        boolean saveDocuments, boolean saveClusters, boolean saveOtherAttributes)
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        if (StringUtils.isNotBlank(callback))
        {
            writer.write(callback + "(");
        }
        final Map<String, Object> attrs = prepareAttributesForSerialization(
            saveDocuments, saveClusters, saveOtherAttributes);

        mapper.writeValue(writer, attrs);
        if (StringUtils.isNotBlank(callback))
        {
            writer.write(");");
        }
    }

    /**
     * Prepares a temporary attributes map for serialization purposes. Includes only the
     * requested elements in the map.
     */
    private Map<String, Object> prepareAttributesForSerialization(boolean saveDocuments,
        boolean saveClusters, boolean saveOtherAttributes)
    {
        final Map<String, Object> tempAttributes = Maps.newHashMap();

        if (saveOtherAttributes)
        {
            tempAttributes.putAll(attributes);
            tempAttributes.remove(AttributeNames.DOCUMENTS);
            tempAttributes.remove(AttributeNames.CLUSTERS);
        }
        else
        {
            tempAttributes
                .put(AttributeNames.QUERY, attributes.get(AttributeNames.QUERY));
        }

        if (saveDocuments)
        {
            tempAttributes.put(AttributeNames.DOCUMENTS, getDocuments());
        }

        if (saveClusters)
        {
            tempAttributes.put(AttributeNames.CLUSTERS, getClusters());
        }

        return tempAttributes;
    }
}
