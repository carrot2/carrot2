
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.carrot2.util.StringUtils;

/**
 * A document that to be processed by the framework. Each document is a collection of
 * fields carrying different bits of information, e.g. {@link #TITLE} or
 * {@link #CONTENT_URL}.
 */
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

    /** Fields of this document */
    private final Map<String, Object> fields = new HashMap<>();

    /** Read-only collection of fields exposed in {@link #getField(String)}. */
    private final Map<String, Object> fieldsView = Collections.unmodifiableMap(fields);

    /**
     * @see #getStringId()
     * @see ProcessingResult
     */
    String id;

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
     * @return identifier of this document, possibly <code>null</code>
     */
    public String getStringId()
    {
        return id;
    }

    /**
     * Returns this document's {@link #TITLE} field.
     */
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
    public Document setTitle(String title)
    {
        return setField(TITLE, title);
    }

    /**
     * Returns this document's {@link #SUMMARY} field.
     */
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
    public Document setSummary(String summary)
    {
        return setField(SUMMARY, summary);
    }

    /**
     * Returns this document's {@link #SOURCES} field.
     */
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
    public Document setScore(Double score)
    {
        return setField(SCORE, score);
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
                final HashSet<String> ids = new HashSet<>();
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
}
