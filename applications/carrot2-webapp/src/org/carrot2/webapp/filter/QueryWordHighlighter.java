
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

package org.carrot2.webapp.filter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;

/**
 * Highlights query words in documents using the &lt;b&gt; HTML tag. Highlighting is
 * performed on the fields specified in {@link #fields}, the results are saved in fields
 * with names suffixed by {@link #HIGHLIGHTED_FIELD_NAME_SUFFIX}.
 */
@Bindable
public class QueryWordHighlighter extends ProcessingComponentBase
{
    /**
     * Suffix appended
     */
    public static final String HIGHLIGHTED_FIELD_NAME_SUFFIX = "-highlight";

    /**
     * Query that produced the documents, optional. If query is blank, no processing will
     * be performed.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.QUERY)
    public String query = null;

    /**
     * {@link Document}s to highlight query words in.
     */
    @Processing
    @Input
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    /**
     * Fields of the {@link Document} that should have the query words highlighted.
     */
    @Init
    @Input
    @Attribute
    public Collection<String> fields = Arrays.asList(new String []
    {
        Document.TITLE, Document.SUMMARY
    });

    @Override
    public void process() throws ProcessingException
    {
        // No processing if query is blank
        if (StringUtils.isBlank(query))
        {
            return;
        }

        // Create regexp patterns for each query word
        final String [] queryWords = query.split("\\s+");
        final Pattern [] queryPatterns = new Pattern [queryWords.length];
        for (int i = 0; i < queryWords.length; i++)
        {
            queryPatterns[i] = Pattern.compile("("
                + Pattern.quote(escapeLtGt(queryWords[i])) + ")",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        }

        for (Document document : documents)
        {
            for (String fieldName : fields)
            {
                highlightQueryTerms(document, fieldName, queryPatterns);
            }
        }
    }

    private void highlightQueryTerms(Document document, String fieldName,
        Pattern [] queryPatterns)
    {
        String field = (String) document.getField(fieldName);

        if (StringUtils.isBlank(field))
        {
            return;
        }

        field = escapeLtGt(field);

        for (Pattern pattern : queryPatterns)
        {
            Matcher matcher = pattern.matcher(field);
            field = matcher.replaceAll("<b>$1</b>");
        }

        document.setField(fieldName + HIGHLIGHTED_FIELD_NAME_SUFFIX, field);
    }

    private static final Pattern LT_PATTERN = Pattern.compile("<");
    private static final Pattern GT_PATTERN = Pattern.compile(">");

    private String escapeLtGt(String field)
    {
        field = LT_PATTERN.matcher(field).replaceAll("&lt;");
        field = GT_PATTERN.matcher(field).replaceAll("&gt;");
        return field;
    }
}
