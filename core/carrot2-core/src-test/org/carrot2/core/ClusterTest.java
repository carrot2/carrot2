
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

package org.carrot2.core;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatDocuments;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.carrot2.util.tests.CarrotTestCase;
import org.fest.assertions.Assertions;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Test cases for {@link Cluster}.
 */
public class ClusterTest extends CarrotTestCase
{
    @Test
    public void testAllDocumentsEmptyFlat()
    {
        final Cluster flatCluster = new Cluster();
        assertEquals(0, flatCluster.size());
        Assertions.assertThat(flatCluster.getAllDocuments()).isEmpty();
    }

    @Test
    public void testAllDocumentsEmptyHierarchical()
    {
        final Cluster hierarchicalCluster = new Cluster();
        final Cluster subcluster = new Cluster();

        hierarchicalCluster.addSubclusters(subcluster);
        subcluster.addSubclusters(new Cluster());

        assertEquals(0, hierarchicalCluster.size());
        Assertions.assertThat(hierarchicalCluster.getAllDocuments()).isEmpty();
    }

    @Test
    public void testSizeNonEmptyFlat()
    {
        final Cluster flatCluster = new Cluster();
        final List<Document> documents = Lists.newArrayList(new Document(),
            new Document());

        flatCluster.addDocuments(documents);
        assertEquals(2, flatCluster.size());
        assertEquals(documents, flatCluster.getAllDocuments());
    }

    @Test
    public void testSizeNonEmptyHierarchicalWithoutOverlap()
    {
        final Cluster hierarchicalCluster = new Cluster();
        final Cluster subcluster = new Cluster();

        hierarchicalCluster.addSubclusters(subcluster);

        final Document documentA = new Document();
        hierarchicalCluster.addDocuments(documentA);
        final Document documentB = new Document();
        subcluster.addDocuments(documentB);

        final List<Document> expectedAllDocuments = Lists.newArrayList(documentA,
            documentB);

        assertEquals(2, hierarchicalCluster.size());
        assertEquals(expectedAllDocuments, hierarchicalCluster.getAllDocuments());
    }

    @Test
    public void testSizeNonEmptyHierarchicalWithOverlap()
    {
        final Cluster hierarchicalCluster = new Cluster();
        final Cluster subcluster = new Cluster();
        hierarchicalCluster.addSubclusters(subcluster);

        final Document document1 = new Document();

        hierarchicalCluster.addDocuments(document1);
        final Document documentB = new Document();
        hierarchicalCluster.addDocuments(documentB);
        subcluster.addDocuments(document1);
        final Document documentC = new Document();
        subcluster.addDocuments(documentC);

        final List<Document> expectedAllDocuments = Lists.newArrayList(document1,
            documentB, documentC);

        assertEquals(3, hierarchicalCluster.size());
        assertEquals(expectedAllDocuments, hierarchicalCluster.getAllDocuments());
    }

    @Test
    public void testByLabelComparator()
    {
        final Cluster clusterA = new Cluster();
        clusterA.addPhrases("A");

        final Cluster clusterB = new Cluster();
        clusterB.addPhrases("b");

        final Cluster clusterNull = new Cluster();

        checkOrder(Lists.newArrayList(clusterNull, clusterA, clusterB),
            Cluster.BY_SIZE_COMPARATOR);
    }

    @Test
    public void testBySizeComparator()
    {
        final Cluster clusterA = new Cluster();
        clusterA.addDocuments(new Document(), new Document());

        final Cluster clusterB = new Cluster();

        checkOrder(Lists.newArrayList(clusterB, clusterA), Cluster.BY_SIZE_COMPARATOR);
    }

    @Test
    public void testByReversedSizeAndLabelComparator()
    {
        final Cluster clusterA = new Cluster();
        clusterA.addPhrases("A");
        clusterA.addDocuments(new Document(), new Document());

        final Cluster clusterB = new Cluster();
        clusterB.addPhrases("B");
        clusterB.addDocuments(new Document(), new Document());

        final Cluster clusterC = new Cluster();
        clusterC.addPhrases("C");
        clusterC.addDocuments(new Document(), new Document(), new Document());

        checkOrder(Lists.newArrayList(clusterC, clusterA, clusterB),
            Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);
    }

    @Test
    public void testByReversedWeightedScoreAndSizeComparatorOnlySize()
    {
        checkOrder(createSizeAndScoreClusters(1, 2, 0), Cluster
            .byReversedWeightedScoreAndSizeComparator(0));
    }

    @Test
    public void testByReversedWeightedScoreAndSizeComparatorOnlyScore()
    {
        checkOrder(createSizeAndScoreClusters(1, 0, 2), Cluster
            .byReversedWeightedScoreAndSizeComparator(1));
    }

    private List<Cluster> createSizeAndScoreClusters(int a, int b, int c)
    {
        Cluster [] clusters = new Cluster [3];
        final Cluster clusterA = new Cluster();
        clusterA.addPhrases("A");
        clusterA.setAttribute(Cluster.SCORE, 1.0);
        clusterA.addDocuments(new Document(), new Document());
        clusters[a] = clusterA;

        final Cluster clusterB = new Cluster();
        clusterB.addPhrases("B");
        clusterB.setAttribute(Cluster.SCORE, 2.0);
        clusterB.addDocuments(new Document(), new Document());
        clusters[b] = clusterB;

        final Cluster clusterC = new Cluster();
        clusterC.addPhrases("C");
        clusterC.setAttribute(Cluster.SCORE, 0.1);
        clusterC.addDocuments(new Document(), new Document(), new Document());
        clusters[c] = clusterC;

        return Arrays.asList(clusters);
    }

    private void checkOrder(List<Cluster> expected, Comparator<Cluster> comparator)
    {
        List<Cluster> toSort = Lists.newArrayList(expected);
        Collections.sort(toSort, comparator);
        Assertions.assertThat(toSort).isEqualTo(expected);
    }

    @Test()
    public void testNoIdentifiers()
    {
        final Cluster d1 = new Cluster();
        final Cluster d2 = new Cluster();
        final Cluster d3 = new Cluster();

        Cluster.assignClusterIds(Lists.newArrayList(d1, d2, d3));
        assertThat(d1.id).isEqualTo(0);
        assertThat(d2.id).isEqualTo(1);
        assertThat(d3.id).isEqualTo(2);
    }

    @Test()
    public void testSubclusterIdentifiers()
    {
        final Cluster d1 = new Cluster();
        final Cluster d2 = new Cluster();
        final Cluster d3 = new Cluster();
        final Cluster d4 = new Cluster();
        d1.addSubclusters(d2);
        d2.addSubclusters(d4);

        Cluster.assignClusterIds(Lists.newArrayList(d1, d3));
        assertThat(d1.id).isEqualTo(0);
        assertThat(d2.id).isEqualTo(1);
        assertThat(d4.id).isEqualTo(2);
        assertThat(d3.id).isEqualTo(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSomeIdentifiers()
    {
        final Cluster d1 = new Cluster();
        d1.id = 2;
        final Cluster d2 = new Cluster();
        final Cluster d3 = new Cluster();
        final Cluster d4 = new Cluster();
        d4.id = 5;
        final Cluster d5 = new Cluster();

        Cluster.assignClusterIds(Lists.newArrayList(d1, d2, d3, d4, d5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonUniqueIdentifiers()
    {
        final Cluster d1 = new Cluster();
        d1.id = 0;
        final Cluster d2 = new Cluster();
        d2.id = 0;

        Cluster.assignClusterIds(Lists.newArrayList(d1, d2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSingleNullIdentifier()
    {
        final Cluster d1 = new Cluster();
        d1.id = 0;
        final Cluster d2 = new Cluster();
        Cluster.assignClusterIds(Lists.newArrayList(d1, d2));
    }

    @Test
    public void testFindRootCluster()
    {
        final Cluster c1 = new Cluster();
        c1.id = 0;
        final Cluster c2 = new Cluster();
        c2.id = 1;

        Assertions.assertThat(Cluster.find(1, Lists.newArrayList(c1, c2))).isSameAs(c2);
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

        Assertions.assertThat(Cluster.find(2, Lists.newArrayList(c1))).isSameAs(c3);
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

        Assertions.assertThat(Cluster.find(3, Lists.newArrayList(c1))).isNull();
    }

    @Test
    public void testBuildOtherTopicsNonAssigned()
    {
        final Document d1 = new Document();
        final Document d2 = new Document();
        final Document d3 = new Document();
        final List<Document> allDocuments = Lists.newArrayList(d1, d2, d3);

        final Cluster c1 = new Cluster();
        final Cluster c2 = new Cluster();
        final Cluster c3 = new Cluster();
        c2.addSubclusters(c3);
        final List<Cluster> clusters = Lists.newArrayList(c1, c2);

        final Cluster otherTopics = Cluster.buildOtherTopics(allDocuments, clusters);

        assertThatDocuments(otherTopics.getDocuments()).isEquivalentTo(allDocuments);
    }

    @Test
    public void testBuildOtherTopicsSomeAssigned()
    {
        final Document d1 = new Document();
        final Document d2 = new Document();
        final Document d3 = new Document();
        final List<Document> allDocuments = Lists.newArrayList(d1, d2, d3);

        final Cluster c1 = new Cluster();
        final Cluster c2 = new Cluster();
        final Cluster c3 = new Cluster();
        c2.addSubclusters(c3);
        c3.addDocuments(d2);
        final List<Cluster> clusters = Lists.newArrayList(c1, c2);

        final Cluster otherTopics = Cluster.buildOtherTopics(allDocuments, clusters);

        assertThatDocuments(otherTopics.getDocuments()).isEquivalentTo(Lists.newArrayList(d1, d3));
    }

    @Test
    public void testBuildOtherTopicsAllAssigned()
    {
        final Document d1 = new Document();
        final Document d2 = new Document();
        final Document d3 = new Document();
        final List<Document> allDocuments = Lists.newArrayList(d1, d2, d3);

        final Cluster c1 = new Cluster();
        final Cluster c2 = new Cluster();
        final Cluster c3 = new Cluster();
        c2.addSubclusters(c3);
        c3.addDocuments(d2);
        c1.addDocuments(d1);
        c2.addDocuments(d3);
        final List<Cluster> clusters = Lists.newArrayList(c1, c2);

        final Cluster otherTopics = Cluster.buildOtherTopics(allDocuments, clusters);

        assertThatDocuments(otherTopics.getDocuments()).isEquivalentTo(
            Lists.<Document> newArrayList());
    }
}
