package org.carrot2.core;

import java.util.*;

import org.simpleframework.xml.*;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

import com.google.common.collect.Maps;

/**
 * A document that to be processed by the framework. Each document is a collection of
 * fields carrying different bits of information, e.g. {@link #TITLE} or
 * {@link #CONTENT_URL}.
 */
@Root(name = "document")
public final class Document
{
    /** Field name for the title of the document. */
    public static final String TITLE = "title";

    /**
     * Field name for a short summary of the document, e.g. the snippet returned by the
     * search engine.
     */
    public static final String SUMMARY = "summary";

    /** Field name for an URL pointing to the full version of the document. */
    public static final String CONTENT_URL = "url";

    /** Fields of this document */
    private final HashMap<String, Object> fields = new HashMap<String, Object>();

    /** Read-only collection of fields exposed in {@link #getField(String)}. */
    private final Map<String, Object> fieldsView = Collections.unmodifiableMap(fields);

    /**
     * Field used during serialization/ deserialization to preserve Carrot2 2.x format.
     * See {@link #beforeSerialization()} and {@link #afterDeserialization()}.
     */
    @Element(required = false)
    private String title;

    /**
     * Field used during serialization/ deserialization to preserve Carrot2 2.x format.
     * See {@link #beforeSerialization()} and {@link #afterDeserialization()}.
     */
    @Element(required = false)
    private String url;

    /**
     * Field used during serialization/ deserialization to preserve Carrot2 2.x format.
     * See {@link #beforeSerialization()} and {@link #afterDeserialization()}.
     */
    @Element(required = false)
    private String snippet;

    /**
     * Field used during serialization/ deserialization to preserve Carrot2 2.x format.
     * See {@link #beforeSerialization()} and {@link #afterDeserialization()}.
     */
    @ElementMap(required = false)
    private HashMap<String, Object> otherFields;

    /**
     * Internal identifier of the document. This identifier is assigned dynamically after
     * documents are returned from {@link DocumentSource}.
     * 
     * @see ProcessingResult
     */
    @Attribute(required = false)
    Integer id;

    /**
     * Creates an empty document with no fields.
     */
    public Document()
    {
    }

    /**
     * A unique identifier of this document. The identifiers are assigned to documents
     * before processing finishes. Note that two documents with equal contents will be
     * assigned different identifiers.
     * 
     * @return unique identifier of this document
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * Returns all fields of this document. The returned map is unmodifiable.
     * 
     * @return all fields of this document
     */
    public Map<String, Object> getFields()
    {
        return fieldsView;
    }

    /**
     * Returns value of the specified field of this document. If no field corresponds to
     * the provided <code>name</code>, <code>null</code> will be returned.
     * 
     * @param name of the field to be returned
     * @return value of the field or <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public <T> T getField(String name)
    {
        return (T) fields.get(name);
    }

    /**
     * Adds a field to this document.
     * 
     * @param name of the field to be added
     * @param value value of the field
     * @return this document for convenience
     */
    public Document addField(String name, Object value)
    {
        fields.put(name, value);
        return this;
    }

    /**
     * A utility method for creating a document with provided <code>title</code>,
     * <code>summary</code> and <code>contentUrl</code>.
     * 
     * @param title for the document
     * @param summary for the document
     * @param contentUrl for the document
     * @return the created document
     */
    public static Document create(String title, String summary, String contentUrl)
    {
        final Document document = new Document();

        document.addField(TITLE, title);
        document.addField(SUMMARY, summary);
        document.addField(CONTENT_URL, contentUrl);

        return document;
    }

    /**
     * Transfers some fields from the map to individual class fields.
     */
    @Persist
    private void beforeSerialization()
    {
        title = (String) fields.get(TITLE);
        snippet = (String) fields.get(SUMMARY);
        url = (String) fields.get(CONTENT_URL);

        otherFields = Maps.newHashMap(fields);
        otherFields.remove(TITLE);
        otherFields.remove(SUMMARY);
        otherFields.remove(CONTENT_URL);
    }

    /**
     * Transfers values of class field to the field map.
     */
    @Commit
    private void afterDeserialization()
    {
        if (otherFields != null)
        {
            fields.putAll(otherFields);
        }
        fields.put(TITLE, title);
        fields.put(SUMMARY, snippet);
        fields.put(CONTENT_URL, url);
    }
}
