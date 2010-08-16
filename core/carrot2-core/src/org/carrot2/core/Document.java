/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.util.MapUtils;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.codehaus.jackson.annotate.*;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

import com.google.common.base.Function;
import com.google.common.collect.*;

/**
 * A document that to be processed by the framework. Each document is a collection of
 * fields carrying different bits of information, e.g. {@link #TITLE} or
 * {@link #CONTENT_URL}.
 */
@Root(name = "document")
@JsonAutoDetect(JsonMethod.NONE)
@JsonWriteNullProperties(false)
public final class Document
{
    /** Field name for the title of the document. */
    public static final String TITLE = "title";

    /**
     * Field name for a short summary of the document, e.g. the snippet returned by the
     * search engine.
     */
    public static final String SUMMARY = "snippet";

    /** Field name for an URL pointing to the full version of the document. */
    public static final String CONTENT_URL = "url";

    /**
     * Click URL. The URL that should be placed in the anchor to the document instead of
     * the value returned in {@link #CONTENT_URL}.
     */
    public static final String CLICK_URL = "click-url";

    /**
     * Field name for an URL pointing to the thumbnail image associated with the document.
     */
    public static final String THUMBNAIL_URL = "thumbnail-url";

    /** Document size. */
    public static final String SIZE = "size";

    /**
     * Field name for a list of sources the document was found in. Value type:
     * <code>List&lt;String&gt;</code>
     */
    public static final String SOURCES = "sources";

    /**
     * Field name for the language in which the document is written. Value type:
     * {@link LanguageCode}. If the <code>language</code> field is not defined or is
     * <code>null</code>, it means the language of the document is unknown or it is
     * outside of the list defined in {@link LanguageCode}.
     */
    public static final String LANGUAGE = "language";

    /**
     * Identifiers of reference clustering partitions this document belongs to. Currently,
     * this field is used only to calculate various clustering quality metrics. In the
     * future, clustering algorithms may be able to use values of this field to increase
     * the quality of clustering.
     * <p>
     * Value type: <code>Collection&lt;Object&gt;</code>. There is no constraint on the
     * actual type of the partition identifier in the collection. Identifiers are assumed
     * to correctly implement the {@link #equals(Object)} and {@link #hashCode()} methods.
     * </p>
     */
    public static final String PARTITIONS = "partitions";

    /** Fields of this document */
    private final Map<String, Object> fields = Maps.newHashMap();

    /** Read-only collection of fields exposed in {@link #getField(String)}. */
    private final Map<String, Object> fieldsView = Collections.unmodifiableMap(fields);

    /**
     * Internal identifier of the document. This identifier is assigned dynamically after
     * documents are returned from {@link IDocumentSource}.
     * 
     * @see ProcessingResult
     */
    @Attribute(required = false)
    Integer id;

    /**
     * Field used during serialization/ deserialization.
     */
    @ElementMap(entry = "field", key = "key", attribute = true, inline = true, required = false)
    private HashMap<String, SimpleXmlWrapperValue> otherFieldsForSerialization;

    /**
     * Creates an empty document with no fields.
     */
    public Document()
    {
    }

    /**
     * Creates a document with the provided <code>title</code>.
     */
    public Document(String title)
    {
        this(title, null);
    }

    /**
     * Creates a document with the provided <code>title</code> and <code>summary</code>.
     */
    public Document(String title, String summary)
    {
        this(title, summary, (String) null);
    }

    /**
     * Creates a document with the provided <code>title</code>, <code>summary</code> and
     * <code>language</code>.
     */
    public Document(String title, String summary, LanguageCode language)
    {
        this(title, summary, null, language);
    }

    /**
     * Creates a document with the provided <code>title</code>, <code>summary</code> and
     * <code>contentUrl</code>.
     */
    public Document(String title, String summary, String contentUrl)
    {
        this(title, summary, contentUrl, null);
    }

    /**
     * Creates a document with the provided <code>title</code>, <code>summary</code>,
     * <code>contentUrl</code> and <code>language</code>.
     */
    public Document(String title, String summary, String contentUrl, LanguageCode language)
    {
        setField(TITLE, title);
        setField(SUMMARY, summary);

        if (StringUtils.isNotBlank(contentUrl))
        {
            setField(CONTENT_URL, contentUrl);
        }

        if (language != null)
        {
            setField(LANGUAGE, language);
        }
    }

    /**
     * A unique identifier of this document. The identifiers are assigned to documents
     * before processing finishes. Note that two documents with equal contents will be
     * assigned different identifiers.
     * 
     * @return unique identifier of this document
     */
    @JsonProperty
    public Integer getId()
    {
        return id;
    }

    /**
     * Returns this document's {@link #TITLE} field.
     */
    @JsonProperty
    @Element(required = false)
    public String getTitle()
    {
        return getField(TITLE);
    }

    /**
     * Sets this document's {@link #TITLE} field.
     * 
     * @param title title to set
     * @return this document for convenience
     */
    @Element(required = false)
    public Document setTitle(String title)
    {
        return setField(TITLE, title);
    }

    /**
     * Returns this document's {@link #SUMMARY} field.
     */
    @JsonProperty("snippet")
    @Element(name = "snippet", required = false)
    public String getSummary()
    {
        return getField(SUMMARY);
    }

    /**
     * Sets this document's {@link #SUMMARY} field.
     * 
     * @param summary summary to set
     * @return this document for convenience
     */
    @Element(name = "snippet", required = false)
    public Document setSummary(String summary)
    {
        return setField(SUMMARY, summary);
    }

    /**
     * Returns this document's {@link #CONTENT_URL} field.
     */
    @JsonProperty("url")
    @Element(name = "url", required = false)
    public String getContentUrl()
    {
        return getField(CONTENT_URL);
    }

    /**
     * Sets this document's {@link #CONTENT_URL} field.
     * 
     * @param contentUrl content URL to set
     * @return this document for convenience
     */
    @Element(name = "url", required = false)
    public Document setContentUrl(String contentUrl)
    {
        return setField(CONTENT_URL, contentUrl);
    }

    /**
     * Returns this document's {@link #SOURCES} field.
     */
    @JsonProperty
    @ElementList(entry = "source", required = false)
    public List<String> getSources()
    {
        return getField(SOURCES);
    }

    /**
     * Sets this document's {@link #SOURCES} field.
     * 
     * @param sources the sources list to set
     * @return this document for convenience
     */
    @ElementList(entry = "source", required = false)
    public Document setSources(List<String> sources)
    {
        return setField(SOURCES, sources);
    }

    /**
     * Returns this document's {@link #LANGUAGE}.
     */
    public LanguageCode getLanguage()
    {
        return getField(LANGUAGE);
    }

    /**
     * Sets this document's {@link #LANGUAGE}.
     * 
     * @param language the language to set
     * @return this document for convenience
     */
    public Document setLanguage(LanguageCode language)
    {
        return setField(LANGUAGE, language);
    }

    @SuppressWarnings("unused")
    @JsonProperty("language")
    @Attribute(required = false, name = "language")
    private String getLanguageIsoCode()
    {
        final LanguageCode language = getLanguage();
        return language != null ? language.getIsoCode() : null;
    }

    @SuppressWarnings("unused")
    @Attribute(required = false, name = "language")
    private void setLanguageIsoCode(String languageIsoCode)
    {
        if (languageIsoCode != null)
        {
            final LanguageCode language = LanguageCode.forISOCode(languageIsoCode);
            if (language != null)
            {
                setLanguage(language);
            }
            else
            {
                // Try by enum name for backward-compatibility
                setLanguage(LanguageCode.valueOf(languageIsoCode));
            }
        }
        else
        {
            setLanguage(null);
        }
    }

    /**
     * For JSON and XML serialization only.
     */
    @JsonProperty("fields")
    @SuppressWarnings("unused")
    private Map<String, Object> getOtherFields()
    {
        final Map<String, Object> otherFields = Maps.newHashMap(fieldsView);
        otherFields.remove(TITLE);
        otherFields.remove(SUMMARY);
        otherFields.remove(CONTENT_URL);
        otherFields.remove(SOURCES);
        otherFields.remove(LANGUAGE);
        return otherFields.isEmpty() ? null : otherFields;
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
        synchronized (fields)
        {
            return (T) fields.get(name);
        }
    }

    /**
     * Sets a field in this document.
     * 
     * @param name of the field to set
     * @param value value of the field
     * @return this document for convenience
     */
    public Document setField(String name, Object value)
    {
        synchronized (fields)
        {
            fields.put(name, value);
        }
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

            // First, find the start value for the id, check uniqueness of the ids
            // already provided and erase duplicated ids.
            int maxId = Integer.MIN_VALUE;
            for (final Document document : documents)
            {
                if (document.id != null)
                {
                    if (ids.add(document.id))
                    {
                        maxId = Math.max(maxId, document.id);
                    }
                    else
                    {
                        document.id = null;
                    }
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
     * Transforms a {@link Document} to its identifier returned by
     * {@link Document#getId()}.
     */
    public static final class DocumentToId implements Function<Document, Integer>
    {
        public static final DocumentToId INSTANCE = new DocumentToId();

        private DocumentToId()
        {
        }

        public Integer apply(Document document)
        {
            return document.id;
        }
    }

    /**
     * Compares {@link Document}s by their identifiers {@link #getId()}, which effectively
     * gives the original order in which they were returned by the document source.
     */
    public static final Comparator<Document> BY_ID_COMPARATOR = Ordering.natural()
        .nullsFirst().onResultOf(DocumentToId.INSTANCE);

    /**
     * Transfers some fields from the map to individual class fields.
     */
    @Persist
    @SuppressWarnings(
    {
        "unused"
    })
    private void beforeSerialization()
    {
        synchronized (fields)
        {
            // Wrapper iterates over the whole map, so we need to synchronize
            // to avoid concurrent modification exceptions in setters
            otherFieldsForSerialization = MapUtils.asHashMap(SimpleXmlWrappers
                .wrap(fields));
            otherFieldsForSerialization.remove(TITLE);
            otherFieldsForSerialization.remove(SUMMARY);
            otherFieldsForSerialization.remove(CONTENT_URL);
            otherFieldsForSerialization.remove(SOURCES);
            otherFieldsForSerialization.remove(LANGUAGE);
        }
    }

    /**
     * Transfers values of class field to the field map.
     */
    @Commit
    @SuppressWarnings("unused")
    private void afterDeserialization() throws Throwable
    {
        synchronized (fields)
        {
            if (otherFieldsForSerialization != null)
            {
                fields.putAll(SimpleXmlWrappers.unwrap(otherFieldsForSerialization));
            }
            otherFieldsForSerialization = null;
        }
    }
}
