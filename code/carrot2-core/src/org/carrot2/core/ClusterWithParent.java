package org.carrot2.core;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Wraps a {@link Cluster} to add information about the cluster's parent cluster, if any.
 * A reference to the parent cluster is not kept directly in the {@link Cluster} class
 * because some algorithms may attach a single cluster to many parent clusters.
 */
public class ClusterWithParent
{
    /** The cluster wrapped by this class */
    public final Cluster cluster;

    /**
     * The parent cluster of {@link #cluster}, may be <code>null</code> in case of
     * top-level clusters.
     */
    public final Cluster parent;

    /**
     * Subclusters of {@link #cluster} wrapped to add their parent cluster. The list is
     * unmodifiable.
     */
    public final List<ClusterWithParent> subclusters;

    /**
     * Private constructor.
     */
    private ClusterWithParent(Cluster parent, Cluster cluster,
        List<ClusterWithParent> subclusters)
    {
        this.parent = parent;
        this.cluster = cluster;
        this.subclusters = subclusters;
    }

    /**
     * Wraps a single <code>cluster</code> together with its parent cluster.
     * 
     * @param cluster the cluster to be wrapped
     * @param parent the parent cluster of <code>cluster</code>, can be
     *            <code>null</code>
     * @return wrapped cluster with parent
     */
    public static ClusterWithParent wrap(Cluster parent, Cluster cluster)
    {
        final List<Cluster> actualSubclusters = cluster.getSubclusters();
        final List<ClusterWithParent> subclustersWithParent = Lists
            .newArrayListWithCapacity(actualSubclusters.size());

        for (Cluster actualCluster : actualSubclusters)
        {
            subclustersWithParent.add(wrap(cluster, actualCluster));
        }

        return new ClusterWithParent(parent, cluster, Collections
            .unmodifiableList(subclustersWithParent));
    }

    /**
     * Wraps a list of top level clusters. Each cluster in the list will be assumed to
     * have a <code>null</code> parent cluster.
     * 
     * @param clusters the list of top-level clusters to be wrapped
     * @return the wrapped clusters with parents
     */
    public static List<ClusterWithParent> wrap(List<Cluster> clusters)
    {
        final List<ClusterWithParent> result = Lists.newArrayListWithCapacity(clusters
            .size());

        for (Cluster cluster : clusters)
        {
            result.add(wrap(null, cluster));
        }

        return result;
    }
}
