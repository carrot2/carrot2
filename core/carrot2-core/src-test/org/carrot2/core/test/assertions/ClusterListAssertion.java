
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test.assertions;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatClusters;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.carrot2.core.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.carrot2.shaded.guava.common.base.Strings;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Assertions on lists of {@link Cluster}s.
 */
public class ClusterListAssertion extends
    GenericListAssertion<ClusterListAssertion, Cluster>
{
    ClusterListAssertion(List<Cluster> actualClusterList)
    {
        super(ClusterListAssertion.class, actualClusterList);
    }

    /**
     * Asserts that the cluster list is equivalent to the provided cluster list. Two lists
     * of clusters are equivalent if they have the same size, and the clusters on the
     * corresponding positions on the lists are equivalent (see
     * {@link ClusterAssertion#isEquivalentTo(Cluster)}.
     * 
     * @param expectedClusterList the expected cluster list
     * @return this assertion for convenience
     */
    public ClusterListAssertion isEquivalentTo(List<Cluster> expectedClusterList)
    {
        return isEquivalentTo(expectedClusterList, true);
    }

    /**
     * Asserts that the cluster list is equivalent to the provided cluster list. Two lists
     * of clusters are equivalent if they have the same size, and the clusters on the
     * corresponding positions on the lists are equivalent (see
     * {@link ClusterAssertion#isEquivalentTo(Cluster, boolean)}.
     * 
     * @param expectedClusterList the expected cluster list
     * @param checkDocuments if <code>false</code>, cluster's document references will not
     *            be checked
     * @return this assertion for convenience
     */
    public ClusterListAssertion isEquivalentTo(List<Cluster> expectedClusterList,
        boolean checkDocuments)
    {
        assertThat(actual).hasSize(expectedClusterList.size());
        for (int i = 0; i < actual.size(); i++)
        {
            assertThat(actual.get(i)).isEquivalentTo(expectedClusterList.get(i),
                checkDocuments);
        }
        return this;
    }

    /**
     * Asserts that there are no Other Topics clusters on the provided list.
     * 
     * @return this assertion for convenience
     */
    public ClusterListAssertion doesNotContainOtherTopicsClusters()
    {
        for (int i = 0; i < actual.size(); i++)
        {
            assertThat(actual.get(i)).isOtherTopics(false);
        }
        return this;
    }

    /**
     * Recursively runs pairwise cluster checks on the actual clusters vs the provided
     * expected clusters hierarchy.
     */
    public ClusterListAssertion passRecursively(List<Cluster> expected,
        ClusterPairCheck... clusterAsserts)
    {
        if (expected == null)
        {
            assertThat(actual).isNull();
        }
        else
        {
            assertThat(actual).isNotNull();
        }

        List<String> expectedLabels = labelList(Lists.<String>newArrayList(), 0, expected);
        List<String> actualLabels = labelList(Lists.<String>newArrayList(), 0, actual);
        if (!actualLabels.equals(expectedLabels))
        {
            tabularizedReport(expectedLabels, actualLabels);
        }
        assertThat(actualLabels).describedAs("Cluster labels").isEqualTo(expectedLabels);

        Iterator<Cluster> expectedIt = expected.iterator();
        Iterator<Cluster> actualIt = actual.iterator();
        while (expectedIt.hasNext() && actualIt.hasNext())
        {
            final Cluster actualCluster = actualIt.next();
            final Cluster expectedCluster = expectedIt.next();
            for (ClusterPairCheck clusterPairAssert : clusterAsserts)
            {
                clusterPairAssert.check(actualCluster, expectedCluster);
            }

            assertThatClusters(actualCluster.getSubclusters()).as(
                description() + ": subclusters of \"" + actualCluster.getLabel() + "\"")
                .passRecursively(expectedCluster.getSubclusters(), clusterAsserts);
        }

        return this;
    }

    private void tabularizedReport(List<String> l1, List<String> l2)
    {
        Logger logger = LoggerFactory.getLogger(ClusterListAssertion.class);

        int maxL1Width = 0;
        for (String s : l1) 
            maxL1Width = Math.max(maxL1Width, s.length());

        final StringBuilder sb = new StringBuilder();
        final Set<String> l1s = Sets.newTreeSet(l1);
        final Set<String> l2s = Sets.newTreeSet(l2);

        if (l1s.equals(l2s))
        {
            sb.append("PROBLEM: Same sets different order or hierarchy.\n");
            
            sb.append("Clusters side-by-side (same-line order changes marked):\n");
            for (int i = 0; i < Math.max(l1.size(), l2.size()); i++)
            {
                String lbl = l1.get(i);
                sb.append(l2.get(i).equals(lbl) ? "  " : "* ");
                sb.append(Strings.padEnd(lbl, maxL1Width, ' '));
                sb.append(" | ");

                lbl = l2.get(i);
                sb.append(l1.get(i).equals(lbl) ? "  " : "* ");
                sb.append(lbl);
                sb.append("\n");
            }
        }
        else
        {
            Set<String> common = Sets.newTreeSet(l1s);
            common.retainAll(l2s);
            l1s.removeAll(common);
            l2s.removeAll(common);

            sb.append("Clusters in the previous set only:\n");
            for (String s : l1s) sb.append("  '" + s + "'\n");
            sb.append("Clusters in the actual set only:\n");
            for (String s : l2s) sb.append("  '" + s + "'\n");
            
            sb.append("Clusters side-by-side (order changes not shown):\n");
            for (int i = 0; i < Math.max(l1.size(), l2.size()); i++)
            {
                String lbl = (i < l1.size() ? l1.get(i) : "--");
                sb.append(l1s.contains(lbl) ? "* " : "  ");
                sb.append(Strings.padEnd(lbl, maxL1Width, ' '));
                sb.append(" | ");

                lbl = (i < l2.size() ? l2.get(i) : "--");
                sb.append(l2s.contains(lbl) ? "* " : "  ");
                sb.append(lbl);
                sb.append("\n");
            }            
        }

        logger.error("Failed cluster list comparison (previous | now):\n" 
            + sb.toString());        
    }

    private List<String> labelList(List<String> list, int indent, List<Cluster> clusters)
    {
        for (Cluster c : clusters)
        {
            list.add(Strings.repeat("   ", indent) + c.getLabel());
            labelList(list, indent + 1, c.getSubclusters());
        }
        return list;
    }

    /**
     * Asserts that the provided checks pass for all clusters on the list, including their
     * subclusters.
     */
    public ClusterListAssertion passRecursively(ClusterCheck... checks)
    {
        for (Cluster cluster : actual)
        {
            for (ClusterCheck clusterCheck : checks)
            {
                clusterCheck.check(cluster);
                assertThatClusters(cluster.getSubclusters()).passRecursively(checks);
            }
        }
        return this;
    }
}
