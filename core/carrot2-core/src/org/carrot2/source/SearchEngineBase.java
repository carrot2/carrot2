package org.carrot2.source;

import java.util.Collection;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.attribute.constraint.NotBlank;

/**
 * A base class facilitating implementation of {@link DocumentSource}s wrapping external
 * search engines. The base class defines the common attribute fields used by more
 * specific base classes and concrete implementations.
 * 
 * @see SimpleSearchEngine
 * @see MultipartSearchEngine
 */
@Bindable
public abstract class SearchEngineBase extends ProcessingComponentBase implements DocumentSource
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

}
