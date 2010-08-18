
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
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
        final ClusterWithParent clusterWithParent = ClusterWithParent.wrap(cluster);

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

        final ClusterWithParent clusterWithParent = ClusterWithParent.wrap(cluster);

        assertNotNull(clusterWithParent);
        assertNull(clusterWithParent.parent);
        assertSame(cluster, clusterWithParent.cluster);
        assertNotNull(clusterWithParent.subclusters);
        assertThat(clusterWithParent.subclusters).hasSize(1);

        assertThat(cluster).isSameAs(clusterWithParent.subclusters.get(0).parent.cluster);
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
    @Test
    public void testCyclicReferences()
    {
        // stack overflow causes the process to exit under .NET.
        Assume.assumeTrue(Platform.getPlatform() == Platform.JAVA);

        try
        {
            final Cluster cluster = new Cluster();
            final Cluster subcluster = new Cluster();
            cluster.addSubclusters(subcluster);
            subcluster.addSubclusters(cluster); // Cyclic reference here
    
            ClusterWithParent.wrap(cluster);
            Assert.fail();
        }
        catch (StackOverflowError e)
        {
            // Expected.
        }
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

    @Test
    public void testFindRootCluster()
    {
        final Cluster c1 = new Cluster();
        c1.id = 0;
        final Cluster c2 = new Cluster();
        c2.id = 1;

        final List<ClusterWithParent> clustersWithParent = ClusterWithParent.wrap(Lists
            .newArrayList(c1, c2));

        assertThat(ClusterWithParent.find(0, clustersWithParent)).isSameAs(
            clustersWithParent.get(0));
    }

    @Test
    public void testFindSubcluster()
    {
        final Cluster c1 = new Cluster();
        c1.id = 0;
        final Cluster c2 = new Cluster();
        c2.id = 1;
        c1.addSubclusters(c2);
        final Cluster c3 = new Cluster();
        c3.id = 2;
        c2.addSubclusters(c3);

        final List<ClusterWithParent> clustersWithParent = ClusterWithParent.wrap(Lists
            .newArrayList(c1));

        assertThat(ClusterWithParent.find(2, clustersWithParent)).isSameAs(
            clustersWithParent.get(0).subclusters.get(0).subclusters.get(0));
    }

    @Test
    public void testFindNotFound()
    {
        final Cluster c1 = new Cluster();
        c1.id = 0;
        final Cluster c2 = new Cluster();
        c2.id = 1;
        c1.addSubclusters(c2);
        final Cluster c3 = new Cluster();
        c3.id = 2;
        c2.addSubclusters(c3);

        final List<ClusterWithParent> clustersWithParent = ClusterWithParent.wrap(Lists
            .newArrayList(c1));

        assertThat(ClusterWithParent.find(4, clustersWithParent)).isNull();
    }

    @Test
    public void testEqualsAndHashCode()
    {
        final Cluster cluster = new Cluster();
        cluster.id = 1;
        final ClusterWithParent c1 = ClusterWithParent.wrap(cluster);
        final ClusterWithParent c2 = ClusterWithParent.wrap(cluster);
        final ClusterWithParent c3 = ClusterWithParent.wrap(new Cluster());
        c1.cluster.id = 3;

        assertThat(c1).isEqualTo(c1);
        assertThat(c1).isEqualTo(c2);
        assertThat(c2).isEqualTo(c1);
        assertThat(c1).isNotEqualTo(c3);
        assertThat(c3).isNotEqualTo(c1);

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode()).isNotEqualTo(c3.hashCode());
    }
}
