
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

import org.carrot2.core.attribute.AttributeNames;

/**
 * Encapsulates the results of processing. Provides access to the values of attributes
 * collected after processing and utility methods for obtaining processed documents (
 * {@link #getDocuments()})) and the created clusters ({@link #getClusters()}).
 */
public final class ProcessingResult extends AttributeSet
{
    /**
     * Creates a {@link ProcessingResult} with the provided 
     * <code>attributes</code>.
     */
    public ProcessingResult(Map<String, Object> attributes)
    {
        super(attributes);

        assert assertListContainsOnly(attributes.get(AttributeNames.DOCUMENTS), Document.class) &&
               assertListContainsOnly(attributes.get(AttributeNames.CLUSTERS), Cluster.class);
    }

    /**
     * Returns the list documents that have been processed or <code>null</code>
     * if no documents were present.
     */
    @SuppressWarnings("unchecked")
    public List<Document> getDocuments()
    {
        return (List<Document>) getAttribute(AttributeNames.DOCUMENTS);
    }

    /**
     * Returns the clusters that have been created during processing or <code>null</code>
     * if none were created.
     */
    @SuppressWarnings("unchecked")
    public List<Cluster> getClusters()
    {
        return (List<Cluster>) attributes.get(AttributeNames.CLUSTERS);
    }
}
