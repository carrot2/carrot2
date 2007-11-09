package org.carrot2.core;

import java.util.Collection;

public interface Cluster
{
    ClusterLabel getLabel();

    Collection<Document> getDocuments();

    Collection<Cluster> getSubclusters();

    // metadata
}
