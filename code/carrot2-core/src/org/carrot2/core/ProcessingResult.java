package org.carrot2.core;

import java.util.Collection;
import java.util.Map;

/**
 * Provides results of query processing.
 * 
 * @see org.carrot2.core.LocalController
 * @author Stanislaw Osinski
 * @version $Revision: 1539 $
 */
public interface ProcessingResult
{
    public Collection<Cluster> getClusters();

    public Collection<Document> getDocuments();

    public Collection<Field> getFields();

    public Map<String, Object> getMetadata();
}