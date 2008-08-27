package org.carrot2.core;

import java.util.*;

import org.carrot2.util.simplexml.TypeStringValuePair;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

import com.google.common.base.Function;
import com.google.common.collect.*;

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

    /** Click URL. The URL that should be placed in the anchor to the document instead
     * of the value returned in {@link #CONTENT_URL}. */
    public static final String CLICK_URL = "click-url";

    /** Field name for an URL pointing to the thumbnail image associated with the document. */
    public static final String THUMBNAIL_URL = "thumbnail-url";

    /** Document size. */
    public static final String SIZE = "size";

    /** Field name for a list of sources the document was found in. Value type: List<String> */
    public static final String SOURCES = "sources";

    /** Fields of this document */
    private Map<String, Object> fields = Maps.newHashMap();

    /** Read-only collection of fields exposed in {@link #getField(String)}. */
    private Map<String, Object> fieldsView = Collections.unmodifiableMap(fields);

    /**
     * Internal identifier of the document. This identifier is assigned dynamically after
     * documents are returned from {@link DocumentSource}.
     * 
     * @see ProcessingResult
     */
    @Attribute(required = false)
    Integer id;

    /**
     * Field used during serialization/ deserialization to preserve Carrot2 2.x format.
     */
    @Element(required = false)
    private String title;

    /**
     * Field used during serialization/ deserialization to preserve Carrot2 2.x format.
     */
    @Element(required = false)
    private String url;

    /**
     * Field used during serialization/ deserialization to preserve Carrot2 2.x format.
     */
    @Element(required = false)
    private String snippet;

    /**
     * Field used during serialization/ deserialization.
     */
    @ElementList(entry = "source", required = false)
    private List<String> sources;

    /**
     * Field used during serialization/ deserialization. 
     */
    @ElementMap(name = "fields", entry = "field", key = "key", value = "value", inline = true, attribute = true, required = false)
    private Map<String, TypeStringValuePair> otherFieldsAsStrings = new HashMap<String, TypeStringValuePair>();

    /**
     * Creates an empty document with no fields.
     */
    public Document()
    {
    }

    /**
     * Creates a document with the provided <code>title</code>, <code>summary</code>
     * and <code>contentUrl</code>.
     */
    public Document(String title, String summary, String contentUrl)
    {
        addField(TITLE, title);
        addField(SUMMARY, summary);
        addField(CONTENT_URL, contentUrl);
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
     * Assigns sequential identifiers to the provided <code>documents</code>. If a
     * document already has an identifier, the identifier will not be changed.
     * 
     * @param documents documents to assign identifiers to.
     * @throws IllegalArgumentException if the provided documents contain non-unique
     *             identifiers
     */
    public static void assignDocumentIds(Collection<Document> documents)
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
                        throw new IllegalArgumentException(
                            "Non-unique document id found: " + document.id);
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
     * Compares {@link Document}s by their identifiers {@link #getId()}, which
     * effectively gives the original order in which they were returned by the document
     * source.
     */
    public static final Comparator<Document> BY_ID_COMPARATOR = Comparators
        .nullLeastOrder(Comparators.fromFunction(new Function<Document, Integer>()
        {
            public Integer apply(Document document)
            {
                return document.id;
            }
        }));

    /**
     * Transfers some fields from the map to individual class fields.
     */
    @Persist
    @SuppressWarnings(
    {
        "unused", "unchecked"
    })
    private void beforeSerialization()
    {
        title = (String) fields.get(TITLE);
        snippet = (String) fields.get(SUMMARY);
        url = (String) fields.get(CONTENT_URL);
        sources = (List<String>) fields.get(SOURCES);

        otherFieldsAsStrings = TypeStringValuePair.toTypeStringValuePairs(fields);
        otherFieldsAsStrings.remove(TITLE);
        otherFieldsAsStrings.remove(SUMMARY);
        otherFieldsAsStrings.remove(CONTENT_URL);
        otherFieldsAsStrings.remove(SOURCES);
    }

    /**
     * Transfers values of class field to the field map.
     */
    @Commit
    @SuppressWarnings("unused")
    private void afterDeserialization() throws Throwable
    {
        if (otherFieldsAsStrings != null)
        {
            fields = TypeStringValuePair.fromTypeStringValuePairs(
                new HashMap<String, Object>(), otherFieldsAsStrings);
        }
        fields.put(TITLE, title);
        fields.put(SUMMARY, snippet);
        fields.put(CONTENT_URL, url);
        if (sources != null)
        {
            fields.put(SOURCES, sources);
        }

        fieldsView = Collections.unmodifiableMap(fields);
        
        sources = null;
    }
}
