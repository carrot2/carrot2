
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
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
import org.codehaus.jackson.*;
import org.codehaus.jackson.impl.DefaultPrettyPrinter;
import org.codehaus.jackson.map.*;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.*;

import com.google.common.base.Function;
import com.google.common.collect.*;

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

    /**
     * Transfers document and cluster lists to the attributes map after deserialization.
     */
    @Commit
    @SuppressWarnings("unused")
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
            final Map<Integer, Document> documentsById = Maps.newHashMap();
            for (Document document : documents)
            {
                documentsById.put(document.id, document);
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
    private void documentIdToReference(Cluster cluster, Map<Integer, Document> documents)
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
     * Serializes this processing result as JSON to the provided <code>writer</code>.
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @throws Exception in case of any problems with serialization
     */
    public void serializeJson(Writer writer) throws IOException
    {
        serializeJson(writer, null);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>.
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @param callback JavaScript function name in which to wrap the JSON response or
     *            <code>null</code>.
     * @throws Exception in case of any problems with serialization
     */
    public void serializeJson(Writer writer, String callback) throws IOException
    {
        serializeJson(writer, callback, true, true);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>.
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @param callback JavaScript function name in which to wrap the JSON response or
     *            <code>null</code>.
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @throws Exception in case of any problems with serialization
     */
    public void serializeJson(Writer writer, String callback, boolean saveDocuments,
        boolean saveClusters) throws IOException
    {
        serializeJson(writer, callback, false, saveDocuments, saveClusters);
    }

    /**
     * Serializes this processing result as JSON to the provided <code>writer</code>.
     * 
     * @param writer the writer to serialize this processing result to. The writer will
     *            <strong>not</strong> be closed.
     * @param callback JavaScript function name in which to wrap the JSON response or
     *            <code>null</code>.
     * @param indent if <code>true</code>, the output JSON will be pretty-printed
     * @param saveDocuments if <code>false</code>, documents will not be serialized.
     * @param saveClusters if <code>false</code>, clusters will not be serialized
     * @throws Exception in case of any problems with serialization
     */
    public void serializeJson(Writer writer, String callback, boolean indent,
        boolean saveDocuments, boolean saveClusters) throws IOException
    {
        final JavaTypeMapper mapper = new JavaTypeMapper();
        mapper.setCustomSerializer(CARROT2_JAVA_TYPES_SERIALIZER);
        final JsonGenerator generator = new JsonFactory().createJsonGenerator(writer);
        if (indent)
        {
            generator.setPrettyPrinter(new DefaultPrettyPrinter());
        }

        final Map<String, Object> mapToSerialize = Maps.newHashMap(attributes);
        if (!saveDocuments)
        {
            mapToSerialize.remove(AttributeNames.DOCUMENTS);
        }
        if (!saveClusters)
        {
            mapToSerialize.remove(AttributeNames.CLUSTERS);
        }

        if (StringUtils.isNotBlank(callback))
        {
            writer.write(callback + "(");
        }
        mapper.write(generator, mapToSerialize);
        if (StringUtils.isNotBlank(callback))
        {
            writer.write(");");
        }
    }

    /**
     * Serializes {@link Document}s as JSON.
     */
    private static class DocumentJavaTypeSerializer extends
        JavaTypeSerializerBase<Document>
    {
        @Override
        public boolean writeAny(JavaTypeSerializer<Object> defaultSerializer,
            JsonGenerator generator, Document document) throws IOException,
            JsonParseException
        {
            // Put all properties in a single map so that Jackson serializes
            // them to a JSON map. Using linked map to preserve order.
            final Map<String, Object> fields = Maps.newLinkedHashMap();
            fields.put("id", document.getId());
            fields.put("title", document.getField(Document.TITLE));
            fields.put("snippet", document.getField(Document.SUMMARY));
            fields.put("url", document.getField(Document.CONTENT_URL));
            fields.putAll(document.getFields());
            defaultSerializer.writeValue(defaultSerializer, generator, fields);
            return true;
        }
    }

    /**
     * Serializes {@link Cluster}s as JSON.
     */
    private static class ClusterJavaTypeSerializer extends
        JavaTypeSerializerBase<Cluster>
    {
        @Override
        public boolean writeAny(JavaTypeSerializer<Object> defaultSerializer,
            JsonGenerator generator, Cluster cluster) throws IOException,
            JsonParseException
        {
            // Put all properties in a single map so that Jackson serializes
            // them to a JSON map. Using linked map to preserve order.
            final Map<String, Object> attributes = Maps.newLinkedHashMap();

            attributes.put("id", cluster.getId());
            final Object score = cluster.getAttribute(Cluster.SCORE);
            if (score != null)
            {
                attributes.put("score", score);
            }
            attributes.put("phrases", cluster.getPhrases());
            attributes.put("documents", Lists.transform(cluster.getDocuments(),
                new Function<Document, Integer>()
                {
                    public Integer apply(Document document)
                    {
                        return document.getId();
                    }
                }));
            if (!cluster.getSubclusters().isEmpty())
            {
                attributes.put("clusters", cluster.getSubclusters());
            }

            // Add any other attributes
            attributes.putAll(cluster.getAttributes());

            defaultSerializer.writeValue(defaultSerializer, generator, attributes);
            return true;
        }
    }

    /**
     * JSON serializer for Carrot2-specific Java types.
     */
    private final static ClassDispatchingJavaTypeSerializer CARROT2_JAVA_TYPES_SERIALIZER = new ClassDispatchingJavaTypeSerializer(
        ImmutableMap.<Class<?>, JavaTypeSerializer<?>> of(Document.class,
            new DocumentJavaTypeSerializer(), Cluster.class,
            new ClusterJavaTypeSerializer()));
}
