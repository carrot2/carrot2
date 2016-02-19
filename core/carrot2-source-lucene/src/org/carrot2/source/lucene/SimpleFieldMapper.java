
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

package org.carrot2.source.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * A simple {@link IFieldMapper} with one-to-one mapping between the default title, url
 * and summary fields.
 */
@Bindable
public class SimpleFieldMapper implements IFieldMapper
{
    /** {@link Group} name. */
    final static String INDEX_FIELD_MAPPING = "Index field mapping";
    /** {@link Group} name. */
    final static String HIGHLIGHTER = "Highlighter";

    /**
     * Document title field name.
     */
    @Input
    @Attribute
    @Init
    @Processing
    @Internal(configuration = true)
    @Label("Document title field")
    @Level(AttributeLevel.BASIC)
    @Group(INDEX_FIELD_MAPPING)
    public String titleField;

    /**
     * Document content field name.
     */
    @Input
    @Attribute
    @Init
    @Processing
    @Internal(configuration = true)
    @Label("Document content field")
    @Level(AttributeLevel.BASIC)
    @Group(INDEX_FIELD_MAPPING)
    public String contentField;

    /**
     * Document URL field name.
     */
    @Input
    @Attribute
    @Init
    @Processing
    @Internal(configuration = true)
    @Label("Document URL field")
    @Level(AttributeLevel.BASIC)
    @Group(INDEX_FIELD_MAPPING)
    public String urlField;

    /**
     * Index search field names. If not specified, title and content fields are used.
     */
    @Input
    @Attribute
    @Init
    @Processing
    @Internal(configuration = true)
    @Label("Search fields")
    @Level(AttributeLevel.MEDIUM)
    @Group(INDEX_FIELD_MAPPING)
    public List<String> searchFields;

    /**
     * Snippet formatter for the highlighter. Highlighter is not used if <code>null</code>.
     */
    @Input
    @Attribute
    @Init
    @Processing
    @ImplementingClasses(classes =
    {
        PlainTextFormatter.class, SimpleHTMLFormatter.class
    }, strict = false)
    @Label("Formatter")
    @Level(AttributeLevel.ADVANCED)
    @Group(HIGHLIGHTER)
    public Formatter formatter = new PlainTextFormatter();

    /**
     * Number of context fragments for the highlighter.
     */
    @Input
    @Attribute
    @Init
    @Processing
    @IntRange(min = 1)
    @Label("Context fragments")
    @Level(AttributeLevel.ADVANCED)
    @Group(HIGHLIGHTER)
    public int contextFragments = 3;

    /**
     * A string used to join context fragments when highlighting.
     */
    @Input
    @Attribute
    @Init
    @Processing
    @Label("Join string")
    @Level(AttributeLevel.ADVANCED)
    @Group(HIGHLIGHTER)
    public String fragmentJoin = "...";

    /**
     * Last initialized highlighter.
     */
    private Highlighter highlighter;

    /**
     * Last received {@link Query} object in
     * {@link #map(Query, Analyzer, Document, org.carrot2.core.Document)}.
     */
    private Query query;

    /*
     * 
     */
    public String [] getSearchFields()
    {
        if (searchFields == null || searchFields.size() == 0)
        {
            ArrayList<String> fields = new ArrayList<String>();
            if (!StringUtils.isEmpty(titleField))
            {
                fields.add(titleField);
            }

            if (!StringUtils.isEmpty(contentField))
            {
                fields.add(contentField);
            }

            return fields.toArray(new String [fields.size()]);
        }
        return searchFields.toArray(new String [searchFields.size()]);
    }

    /*
     * 
     */
    public void map(Query luceneQuery, Analyzer analyzer, Document luceneDoc,
        org.carrot2.core.Document doc)
    {
        if (luceneQuery != query)
        {
            this.query = luceneQuery;
            resetHighlighter();
        }

        /*
         * Map title and url
         */
        String value = fieldValue(titleField, luceneDoc);
        if (value != null)
        {
            doc.setField(org.carrot2.core.Document.TITLE, value);
        }

        value = fieldValue(urlField, luceneDoc);
        if (value != null)
        {
            doc.setField(org.carrot2.core.Document.CONTENT_URL, value);
        }

        /*
         * Map content field.
         */
        value = fieldValue(contentField, luceneDoc);
        if (value != null)
        {
            try
            {
                final String summary;
                if (this.highlighter != null)
                {
                    final String [] fragments = highlighter.getBestFragments(analyzer,
                        contentField, value, contextFragments);

                    if (fragments.length > 0)
                    {
                        summary = StringUtils.join(fragments, fragmentJoin);
                    }
                    else
                    {
                        summary = value;
                    }
                }
                else
                {
                    summary = value;
                }
                doc.setField(org.carrot2.core.Document.SUMMARY, summary);
            }
            catch (IOException e)
            {
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
            catch (InvalidTokenOffsetsException e)
            {
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
        }
    }

    /*
     * 
     */
    private String fieldValue(String fieldName, Document doc)
    {
        if (StringUtils.isEmpty(fieldName))
        {
            return null;
        }

        StringBuilder builder = null;
        for (IndexableField field : doc.getFields())
        {
            if (field.name().equals(fieldName))
            {
                if (builder == null) builder = new StringBuilder();
                if (builder.length() > 0) builder.append(" ");

                builder.append(field.stringValue());
            }
        }

        return builder == null ? null : builder.toString();
    }

    /*
     * 
     */
    private void resetHighlighter()
    {
        if (formatter != null)
        {
            this.highlighter = new Highlighter(formatter, new QueryScorer(query));
            this.highlighter.setEncoder(new DefaultEncoder());
        }
        else
        {
            this.highlighter = null;
        }
    }
}
