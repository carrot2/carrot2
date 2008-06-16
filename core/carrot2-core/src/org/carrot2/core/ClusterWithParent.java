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
    public final ClusterWithParent parent;

    /**
     * Subclusters of {@link #cluster} wrapped to add their parent cluster. The list is
     * unmodifiable.
     */
    public final List<ClusterWithParent> subclusters;

    /**
     * Private constructor.
     */
    private ClusterWithParent(ClusterWithParent parent, Cluster cluster,
        List<ClusterWithParent> subclusters)
    {
        this.parent = parent;
        this.cluster = cluster;
        this.subclusters = subclusters;
    }

    /**
     * Wraps a cluster hierarchy starting with the <code>root</code> into
     * {@link ClusterWithParent} objects. All children of the <code>root</code> cluster
     * will have the {@link #parent} field set to the wrapper corresponding to their
     * parent clusters, while for the <code>root</code> cluster, {@link #parent} will be
     * <code>null</code>. Note that for efficiency reasons, reference cycles are
     * <strong>not</strong> detected.
     * 
     * @param root the cluster to be wrapped
     * @return wrapped cluster with parent
     */
    public static ClusterWithParent wrap(Cluster root)
    {
        return wrap(null, root);
    }

    /**
     * Private method that does the actual wrapping.
     */
    private static ClusterWithParent wrap(ClusterWithParent parent, Cluster root)
    {
        final List<Cluster> actualSubclusters = root.getSubclusters();
        final List<ClusterWithParent> subclustersWithParent = Lists
            .newArrayListWithCapacity(actualSubclusters.size());

        final ClusterWithParent rootWithParent = new ClusterWithParent(parent, root,
            Collections.unmodifiableList(subclustersWithParent));

        for (Cluster actualCluster : actualSubclusters)
        {
            subclustersWithParent.add(wrap(rootWithParent, actualCluster));
        }

        return rootWithParent;
    }

    /**
     * Wraps a list of top level clusters. Each cluster in the list will be assumed to
     * have a <code>null</code> parent cluster. Note that for efficiency reasons,
     * reference cycles are <strong>not</strong> detected.
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
            result.add(wrap(cluster));
        }

        return result;
    }
}
