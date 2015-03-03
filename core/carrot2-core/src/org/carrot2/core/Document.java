
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Attribute;

/**
 * A document that to be processed by the framework. 
 */
public final class Document extends AttributeSet
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

    /**
     * The identifier of this document.
     * 
     * <p>
     * Type of this attribute is a {@link String}.
     * </p>
     */
    public static final String ID = "id";

    /**
     * Creates an empty document with no fields.
     */
    public Document(Map<String, Object> attributes)
    {
        super(attributes);
        
        assert assertListContainsOnly(attributes.get(SOURCES), String.class);
    }

    /**
     * Identifier of this document. The semantics of the identifier varies depending on
     * the {@link IDocumentSource} that produced the documents.
     * 
     * @return identifier of this document, possibly <code>null</code>.
     */
    // TODO: rename to getId
    public String getStringId()
    {
        return getAttribute(ID, String.class);
    }

    /**
     * Returns this document's {@link #TITLE} field.
     */
    public String getTitle()
    {
        return getAttribute(TITLE, String.class);
    }

    /**
     * Returns this document's {@link #SUMMARY} field.
     */
    public String getSummary()
    {
        return getAttribute(SUMMARY, String.class);
    }

    /**
     * Returns this document's {@link #CONTENT_URL} field.
     */
    public String getContentUrl()
    {
        return getAttribute(CONTENT_URL, String.class);
    }

    /**
     * Returns this document's {@link #SOURCES} field.
     */
    @SuppressWarnings("unchecked")
    public List<String> getSources()
    {
        return (List<String>) getAttribute(SOURCES);
    }

    /**
     * Returns this document's {@link #LANGUAGE}.
     */
    public LanguageCode getLanguage()
    {
        return getAttribute(LANGUAGE, LanguageCode.class);
    }

    /**
     * Returns this document's {@link #SCORE}.
     */
    @Attribute(name = "score", required = false)
    public Double getScore()
    {
        return getAttribute(SCORE, Double.class);
    }
}
