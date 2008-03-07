package org.carrot2.core;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Test cases for {@link ClusterWithParent}.
 */
public class ClusterWithParentTest
{
    @Test
    public void testNoSubclusters()
    {
        final Cluster cluster = new Cluster();
        final ClusterWithParent clusterWithParent = ClusterWithParent.wrap(null, cluster);

        assertNotNull(clusterWithParent);
        assertNull(clusterWithParent.parent);
        assertSame(cluster, clusterWithParent.cluster);
        assertNotNull(clusterWithParent.subclusters);
        assertThat(clusterWithParent.subclusters).isEmpty();
    }

    @Test
    public void testDisjointSubclusters()
    {
        final Cluster cluster = new Cluster();
        final Cluster subclusterA = new Cluster();
        cluster.addSubclusters(subclusterA);

        final ClusterWithParent clusterWithParent = ClusterWithParent.wrap(null, cluster);

        assertNotNull(clusterWithParent);
        assertNull(clusterWithParent.parent);
        assertSame(cluster, clusterWithParent.cluster);
        assertNotNull(clusterWithParent.subclusters);
        assertThat(clusterWithParent.subclusters).hasSize(1);

        assertThat(cluster).isSameAs(clusterWithParent.subclusters.get(0).parent);
        assertThat(subclusterA).isSameAs(clusterWithParent.subclusters.get(0).cluster);
        assertThat(clusterWithParent.subclusters.get(0).subclusters).isEmpty();
    }

    @Test
    public void testCommonSubcluster()
    {
        final Cluster clusterA = new Cluster();
        final Cluster clusterB = new Cluster();

        final Cluster subcluster = new Cluster();
        clusterA.addSubclusters(subcluster);
        clusterB.addSubclusters(subcluster);

        final List<ClusterWithParent> clustersWithParents = ClusterWithParent.wrap(Lists
            .newArrayList(clusterA, clusterB));

        assertNotNull(clustersWithParents);
        assertThat(
            Lists.transform(clustersWithParents,
                new Function<ClusterWithParent, Cluster>()
                {
                    public Cluster apply(ClusterWithParent clusterWithParent)
                    {
                        return clusterWithParent.cluster;
                    }
                })).containsOnly(clusterA, clusterB);

        assertThat(subcluster).isSameAs(
            clustersWithParents.get(0).subclusters.get(0).cluster);
        assertThat(subcluster).isSameAs(
            clustersWithParents.get(1).subclusters.get(0).cluster);
    }

    /**
     * Currently, cycle detection is not supported.
     */
    @Test(expected = StackOverflowError.class)
    public void testCyclicReferences()
    {
        final Cluster cluster = new Cluster();
        final Cluster subcluster = new Cluster();
        cluster.addSubclusters(subcluster);
        subcluster.addSubclusters(cluster); // Cyclic reference here

        ClusterWithParent.wrap(null, cluster);
    }

    @Test
    public void testEmptyList()
    {
        final List<Cluster> clusters = Lists.newArrayList();
        final List<ClusterWithParent> clustersWithParents = ClusterWithParent
            .wrap(clusters);

        assertNotNull(clustersWithParents);
        assertThat(clustersWithParents).isEmpty();
    }
}
