package org.carrot2.core;

import java.util.Collection;

/**
 * Assists in building up a {@link ProcessingResult}. 
 */
public final class ProcessingResultBuilder extends AttributeSetBuilder<ProcessingResult>
{
    public ProcessingResultBuilder documents(Collection<Document> documents) { return attr(ProcessingResult.DOCUMENTS, documents); }
    public ProcessingResultBuilder clusters(Collection<Cluster> clusters) { return attr(ProcessingResult.CLUSTERS, clusters); }
    public ProcessingResultBuilder query(String query) { return attr(ProcessingResult.QUERY, query); }

    @Override
    protected ProcessingResultBuilder attr(String key, Object value)
    {
        return (ProcessingResultBuilder) super.attr(key, value);
    }

    @Override
    public ProcessingResult build()
    {
        return new ProcessingResult(cloneAndClearAttributes());
    }
}