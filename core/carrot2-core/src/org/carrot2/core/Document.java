
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.util.MapUtils;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Ordering;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * A document that to be processed by the framework. Each document is a collection of
 * fields carrying different bits of information, e.g. {@link #TITLE} or
 * {@link #CONTENT_URL}.
 */
@Root(name = "document")
@JsonAutoDetect(
    creatorVisibility  = JsonAutoDetect.Visibility.NONE,
    fieldVisibility    = JsonAutoDetect.Visibility.NONE,
    getterVisibility   = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility   = JsonAutoDetect.Visibility.NONE)
@JsonSerialize()
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Document implements Cloneable
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
     * Document score. The semantics of the score depends on the specific document source.
     * Some document sources may not provide document scores at all.
     */
    public static final String SCORE = "score";

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
     * @see #getStringId()
     * @see ProcessingResult
     */
    @Attribute(required = false)
    String id;

    /**
     * Listeners to be notified before this document gets serialized.
     */
    private ArrayList<IDocumentSerializationListener> serializationListeners;

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
     * Creates a document with the provided <code>title</code>, <code>summary</code>,
     * <code>contentUrl</code> and <code>language</code> and ID. IDs should be unique
     * for clustering. If all documents passed for clustering have null IDs then
     * IDs are automatically generated. 
     */
    public Document(String title, String summary, String contentUrl, LanguageCode language, String id)
    {
        this(title, summary, contentUrl, language);
        this.id = id;
    }

    /**
     * @deprecated please use {@link #getStringId()} instead. Currently, this method
     *             attempts to parse the string identifier returned by
     *             {@link #getStringId()} into an integer.
     * @throws NumberFormatException if the identifier could not be converted to an integer
     *             number
     */
    public Integer getId()
    {
        try {
            return id != null ? Integer.parseInt(id) : null;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Could not parse document identifier as an integer: " + id);
        }
    }

    /**
     * Identifier of this document. The semantics of the identifier varies depending on
     * the {@link IDocumentSource} that produced the documents.
     * <p>
     * When processing documents produced by Carrot2-provided {@link IDocumentSource}, the
     * framework generates unique integer identifiers for all the documents. However, when
     * XML document sets are loaded using the
     * {@link ProcessingResult#deserialize(java.io.InputStream)} or
     * {@link ProcessingResult#deserialize(CharSequence)} methods, the original document
     * identifiers are preserved, which means they may be non-unique or not present at all.
     * </p>
     * 
     * @return identifier of this document, possibly <code>null</code>
     */
    @JsonProperty("id")
    public String getStringId()
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

    /**
     * Returns this document's {@link #SCORE}.
     * 
     * @return this document's {@link #SCORE}.
     */
    @Attribute(name = "score", required = false)
    public Double getScore()
    {
        return getField(SCORE);
    }

    /**
     * Sets this document's {@link #SCORE}.
     * 
     * @param score the {@link #SCORE} to set
     * @return this document for convenience.
     */
    @Attribute(name = "score", required = false)
    public Document setScore(Double score)
    {
        return setField(SCORE, score);
    }

    @JsonProperty("language")
    @Attribute(required = false, name = "language")
    private String getLanguageIsoCode()
    {
        final LanguageCode language = getLanguage();
        return language != null ? language.getIsoCode() : null;
    }

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
    private Map<String, Object> getOtherFields()
    {
        final Map<String, Object> otherFields;

        // If a caching controller is used, concurrent threads can operate on the same
        // instance of the Document class, so we need to synchronize here to avoid
        // ConcurrentModificationExceptions.
        synchronized (this)
        {
            otherFields = Maps.newHashMap(fields);
        }
        otherFields.remove(TITLE);
        otherFields.remove(SUMMARY);
        otherFields.remove(CONTENT_URL);
        otherFields.remove(SOURCES);
        otherFields.remove(LANGUAGE);
        otherFields.remove(SCORE);
        fireSerializationListeners(otherFields);
        return otherFields.isEmpty() ? null : otherFields;
    }

    /*
     * 
     */
    @ElementMap(entry = "field", key = "key", attribute = true, inline = true, required = false)
    private HashMap<String, SimpleXmlWrapperValue> getOtherFieldsXml()
    {
        final HashMap<String, SimpleXmlWrapperValue> otherFieldsForSerialization;
        synchronized (this)
        {
            otherFieldsForSerialization = MapUtils.asHashMap(SimpleXmlWrappers
                .wrap(fields));
        }
        otherFieldsForSerialization.remove(TITLE);
        otherFieldsForSerialization.remove(SUMMARY);
        otherFieldsForSerialization.remove(CONTENT_URL);
        otherFieldsForSerialization.remove(SOURCES);
        otherFieldsForSerialization.remove(LANGUAGE);
        otherFieldsForSerialization.remove(SCORE);
        fireSerializationListeners(otherFieldsForSerialization);
        return otherFieldsForSerialization.isEmpty() ? null : otherFieldsForSerialization;
    }

    /*
     * 
     */
    @ElementMap(entry = "field", key = "key", attribute = true, inline = true, required = false)
    private void setOtherFieldsXml(
        HashMap<String, SimpleXmlWrapperValue> otherFieldsForSerialization)
    {
        if (otherFieldsForSerialization != null)
        {
            // No need to synchronize here, the object is being deserialized,
            // so it can't yet be seen by other threads.
            fields.putAll(SimpleXmlWrappers.unwrap(otherFieldsForSerialization));
        }
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
        synchronized (this)
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
        synchronized (this)
        {
            fields.put(name, value);
        }
        return this;
    }
    
    /**
     * Creates a <strong>shallow</strong> clone of itself. The identifier
     * and the fields map is copied but values inside fields are not cloned. 
     */
    @Override
    public Document clone()
    {
        Document clone = new Document();
        clone.id = this.id;
        clone.fields.putAll(this.fields);
        return clone;
    }

    /**
     * Assigns sequential identifiers to the provided <code>documents</code>. If any
     * document in the set has a non-empty identifier, no identifiers will be generated at
     * all.
     * 
     * @param documents documents to assign identifiers to.
     * @throws IllegalArgumentException Thrown if the collection of documents already contains
     *              identifiers and they are not unique.
     */
    public static void assignDocumentIds(Collection<Document> documents)
    {
        // We may get concurrent calls referring to the same documents
        // in the same list, so we need to synchronize here.
        synchronized (documents)
        {
            // Make sure there are no identifiers. Or if they are present, they should be unique.
            boolean hadIds = false;
            for (Document document : documents)
            {
                if (document.id != null)
                {
                    hadIds = true;
                    break;
                }
            }

            if (hadIds)
            {
                final HashSet<String> ids = Sets.newHashSet();
                for (Document doc : documents)
                {
                    String id = doc.getStringId();
                    if (!ids.add(id) && id != null)
                    {
                      throw new IllegalArgumentException(
                          "Identifiers must be unique, duplicated identifier: " + id + 
                          " [existing: " + ids.toString() + "]");
                    }
                }

                if (ids.contains(null))
                {
                    throw new IllegalArgumentException(
                        "Null identifiers cannot be mixed with existing non-null identifiers: " +
                        " [existing: " + ids.toString() + "]");
                }
            }
            else
            {
                // All nulls, assign ids.
                int id = 0;
                for (final Document document : documents)
                {
                    document.id = Integer.toString(id);
                    id++;
                }
            }
        }
    }

    /**
     * Transforms a {@link Document} to its identifier returned by
     * {@link Document#getId()}.
     * 
     * @deprecated Please use #getStringId() directly or use your own {@link Function}
     *             implementation.
     */
    public static final class DocumentToId implements Function<Document, Integer>
    {
        public static final DocumentToId INSTANCE = new DocumentToId();

        private DocumentToId()
        {
        }

        public Integer apply(Document document)
        {
            return document.getId();
        }
    }

    /**
     * Compares {@link Document}s by their identifiers {@link #getId()}, which effectively
     * gives the original order in which they were returned by the document source.
     * 
     * @deprecated semantics of the identifiers depends on the document source, please
     *             roll your own comparator that is aware of the actual id semantics.
     */
    public static final Comparator<Document> BY_ID_COMPARATOR = Ordering.natural()
        .nullsFirst().onResultOf(DocumentToId.INSTANCE);

    /**
     * Adds a serialization listener to this document.
     * 
     * @param listener the listener to add
     */
    public void addSerializationListener(IDocumentSerializationListener listener)
    {
        synchronized (this)
        {
            if (serializationListeners == null)
            {
                serializationListeners = Lists.newArrayList();
            }
            serializationListeners.add(listener);
        }
    }

    /**
     * Enables listening to events related to XML/JSON serialization of {@link Document}s.
     */
    public static interface IDocumentSerializationListener
    {
        /**
         * Called before a {@link Document} gets serialized to XML or JSON. Specific
         * implementations may want to modify some properties of the document before it
         * gets serialized
         * 
         * @param document the documents being serialized. Note: changes to the document
         *            will not be undone after serialization completes.
         * @param otherFieldsForSerialization custom fields that are about to be
         *            serialized. Changes made on this map will not affect the contents of
         *            the document.
         */
        public void beforeSerialization(Document document,
            Map<String, ?> otherFieldsForSerialization);
    }

    private void fireSerializationListeners(Map<String, ?> otherFieldsForSerialization)
    {
        synchronized (this)
        {
            if (serializationListeners != null)
            {
                for (IDocumentSerializationListener listener : serializationListeners)
                {
                    listener.beforeSerialization(this, otherFieldsForSerialization);
                }
            }
        }
    }
}
