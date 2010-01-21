package org.carrot2.text.clustering;

import java.util.List;

import org.carrot2.core.*;

/**
 * An internal interface of an algorithm performing clustering in one language.
 */
public interface IMonolingualClusteringAlgorithm
{
    /**
     * Clusters <code>documents</code> assuming they are written in <code>language</code>.
     */
    public List<Cluster> process(List<Document> documents, LanguageCode language);
}
