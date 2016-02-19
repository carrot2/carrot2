
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

package org.carrot2.source;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.attribute.constraint.NotBlank;

/**
 * A base class facilitating implementation of {@link IDocumentSource}s wrapping external
 * search engines with remote/ network-based interfaces. The base class defines the common
 * attribute fields used by more specific base classes and concrete implementations.
 * 
 * @see SimpleSearchEngine
 * @see MultipageSearchEngine
 */
@Bindable(prefix = "SearchEngineBase", inherit = CommonAttributes.class)
public abstract class SearchEngineBase extends ProcessingComponentBase implements
    IDocumentSource
{
    /** {@link Group} name. */
    public static final String SERVICE = "Service";

    /** {@link Group} name. */
    protected static final String POSTPROCESSING = "Postprocessing";
    
    
    @Processing
    @Input
    @Attribute(key = AttributeNames.START, inherit = true)
    @IntRange(min = 0)
    public int start = 0;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS, inherit = true)
    @IntRange(min = 1)
    public int results = 100;

    @Processing
    @Input
    @Attribute(key = AttributeNames.QUERY, inherit = true)
    @Required
    @NotBlank
    public String query;

    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL, inherit = true)
    public long resultsTotal;

    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    @Internal
    public Collection<Document> documents;

    /**
     * Indicates whether the search engine returned a compressed result stream.
     */
    @Processing
    @Output
    @Attribute
    @Label("Compression used")
    @Group(DefaultGroups.RESULT_INFO)
    public boolean compressed;

    /**
     * This component usage statistics.
     */
    public SearchEngineStats statistics = new SearchEngineStats();

    /**
     * Regexp pattern for matching query word highlighting.
     */
    private static Pattern HIGHLIGHTS_PATTERN = Pattern.compile("</?b>");

    /**
     * Unescape HTML entities and tags from a given set of <code>fields</code> of all
     * documents in the provided <code>response</code>.
     * 
     * @param response the search engine response to clean
     * @param keepHighlights set to <code>true</code> to keep query terms highlights
     * @param fields names of fields to clean
     */
    protected static void clean(SearchEngineResponse response, boolean keepHighlights,
        String... fields)
    {
        for (Document document : response.results)
        {
            for (String field : fields)
            {
                final String originalField = document.getField(field);
                if (StringUtils.isNotBlank(originalField))
                {
                    String cleanedField = originalField;
                    if (!keepHighlights)
                    {
                        final Matcher matcher = HIGHLIGHTS_PATTERN.matcher(cleanedField);
                        cleanedField = matcher.replaceAll("");
                    }

                    cleanedField = StringEscapeUtils.unescapeHtml(cleanedField);

                    document.setField(field, cleanedField);
                }
            }
        }
    }

    /**
     * Called after a single search engine response has been fetched. The concrete
     * implementation may want to override this empty implementation to e.g., clean or
     * otherwise postprocess the returned results.
     */
    protected void afterFetch(SearchEngineResponse response)
    {
    }

    /**
     * URL-encodes a string into UTF-8.
     */
    protected static final String urlEncode(String string)
    {
        return org.carrot2.util.StringUtils.urlEncodeWrapException(string, "UTF-8");
    }
}
