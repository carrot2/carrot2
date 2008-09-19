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
 * A base class facilitating implementation of {@link DocumentSource}s wrapping external
 * search engines. The base class defines the common attribute fields used by more
 * specific base classes and concrete implementations.
 * 
 * @see SimpleSearchEngine
 * @see MultipageSearchEngine
 */
@Bindable(prefix = "SearchEngineBase")
public abstract class SearchEngineBase extends ProcessingComponentBase implements
    DocumentSource
{
    /**
     * Starting index of the first result to fetch.
     * 
     * @group Results paging
     * @label Start index
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.START)
    @IntRange(min = 0)
    public int start = 0;

    /**
     * Number of results to fetch.
     * 
     * @group Results paging
     * @label Results count
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @IntRange(min = 1)
    public int results = 100;

    /**
     * Search query to execute.
     * 
     * @group Search query
     * @label Query
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.QUERY)
    @Required
    @NotBlank
    public String query;

    /**
     * Number of total matching documents. This may be an approximation.
     * 
     * @label Results Total
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL)
    public long resultsTotal;

    /**
     * A collection of documents retrieved for the query.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    @Internal
    public Collection<Document> documents;

    /**
     * This component usage statistics.
     */
    public SearchEngineStats statistics = new SearchEngineStats();

    /**
     * Indicates whether the search engine returned a compressed result stream.
     */
    @Processing
    @Output
    @Attribute
    public boolean compressed;

    /**
     * Regexp pattern for matching query word highlighting.
     */
    private static Pattern HIGHLIGHTS_PATTERN = Pattern.compile("</?b>");

    /**
     * Cleans <code>fields</code> of all documents in the provided <code>response</code>.
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

                    document.addField(field, cleanedField);
                }
            }
        }
    }

    /**
     * Called after a single search engine response has been fetched. The concrete
     * implementation may want to override this empty implementation to e.g. clean or
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
