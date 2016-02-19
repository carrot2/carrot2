
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

package org.carrot2.webapp.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Document;
import org.carrot2.core.IControllerContext;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;

import org.carrot2.shaded.guava.common.base.Joiner;
import org.carrot2.shaded.guava.common.base.Strings;
import org.carrot2.shaded.guava.common.collect.Lists;

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
     * Enable or disable query highlighter.
     */
    @Init
    @Processing
    @Input
    @Attribute(key = "QueryWordHighlighter.enabled")
    public boolean enabled = true;

    /**
     * Enable or disable query highlighter.
     */
    @Init
    @Processing
    @Input
    @Attribute(key = "QueryWordHighlighter.maxContentLength")
    public int maxContentLength = Integer.MAX_VALUE;

    
    /**
     * A regular expression that disables highlighting for certain terms.
     */
    @Init
    @Input
    @Attribute(key = "QueryWordHighlighter.dontHighlightPattern")
    public String dontHighlightPattern;
    private Pattern dontHighlightPatternCompiled;

    /**
     * Query-sanitize pattern (any matches are replaced with an empty string).
     */
    @Init
    @Input
    @Attribute(key = "QueryWordHighlighter.querySanitizePattern")
    public String querySanitizePattern = "[\"'()]";
    private Pattern querySanitizePatternCompiled;

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
     * {@link org.carrot2.core.Document}s to highlight query words in.
     */
    @Processing
    @Input
    @Output
    @Required
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    /**
     * Fields of the {@link org.carrot2.core.Document} that should have the query words highlighted.
     */
    @Init
    @Input
    @Attribute
    public Collection<String> fields = Arrays.asList(new String []
    {
        Document.TITLE, Document.SUMMARY
    });
    
    @Override
    public void init(IControllerContext context)
    {
        super.init(context);
        
        if (dontHighlightPattern != null) {
            dontHighlightPatternCompiled = Pattern.compile(dontHighlightPattern);
        }
        
        if (querySanitizePattern != null) {
            querySanitizePatternCompiled = Pattern.compile(querySanitizePattern);
        }
    }

    @Override
    public void process() throws ProcessingException
    {
        if (!enabled)
        {
            return;
        }
        
        if (query == null) 
        {
            query = "";
        }

        // Create regexp patterns for each query word
        final String [] queryTerms = querySanitizePatternCompiled
            .matcher(query).replaceAll("")
            .split("\\s+");

        Pattern queryPattern = null;
        List<String> patterns = Lists.newArrayList();
        for (String queryTerm : queryTerms)
        {
            if (Strings.isNullOrEmpty(queryTerm))
            { 
                continue;
            }
            
            if (dontHighlightPatternCompiled != null && 
                dontHighlightPatternCompiled.matcher(queryTerm).matches())
            {
                continue;
            }

            patterns.add("(" + Pattern.quote(escapeLtGt(queryTerm)) + ")");
        }
        
        if (patterns.size() > 0)
        {
            queryPattern = Pattern.compile(
                Joiner.on("|").join(patterns), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); 
        }

        // As we're going to modify documents, we need to copy them to
        // avoid ConcurrentModificationExceptions.
        final List<Document> inputDocuments = documents;
        final List<Document> outputDocuments = Lists
            .newArrayListWithCapacity(inputDocuments.size());
        
        for (Document document : inputDocuments)
        {
            final Document clonedDocument = document.clone();
            for (String fieldName : fields)
            {
                highlightQueryTerms(clonedDocument, fieldName, queryPattern);
            }
            outputDocuments.add(clonedDocument);
        }
        documents = outputDocuments;
    }

    private void highlightQueryTerms(Document document, String fieldName, Pattern queryPattern)
    {
        String field = (String) document.getField(fieldName);

        if (StringUtils.isBlank(field))
        {
            return;
        }
        
        if (field.length() > maxContentLength) 
        {
            field = field.substring(0, maxContentLength) + "...";
        }

        field = escapeLtGt(field);
        if (queryPattern != null) {
            Matcher matcher = queryPattern.matcher(field);
            field = matcher.replaceAll("<b>$0</b>");
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
