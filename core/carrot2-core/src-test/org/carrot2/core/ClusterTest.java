package org.carrot2.core;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link Cluster}.
 */
public class ClusterTest
{
    @Test
    public void testAllDocumentsEmptyFlat()
    {
        final Cluster flatCluster = new Cluster();
        assertEquals(0, flatCluster.size());
        assertThat(flatCluster.getAllDocuments()).isEmpty();
    }

    @Test
    public void testAllDocumentsEmptyHierarchical()
    {
        final Cluster hierarchicalCluster = new Cluster();
        final Cluster subcluster = new Cluster();

        hierarchicalCluster.addSubclusters(subcluster);
        subcluster.addSubclusters(new Cluster());

        assertEquals(0, hierarchicalCluster.size());
        assertThat(hierarchicalCluster.getAllDocuments()).isEmpty();
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

        assertEquals(0, Cluster.BY_LABEL_COMPARATOR.compare(clusterA, clusterA));
        assertTrue(Cluster.BY_LABEL_COMPARATOR.compare(clusterA, clusterB) < 0);
        assertTrue(Cluster.BY_LABEL_COMPARATOR.compare(clusterNull, clusterB) < 0);
    }

    @Test
    public void testBySizeComparator()
    {
        final Cluster clusterA = new Cluster();
        clusterA.addDocuments(new Document(), new Document());

        final Cluster clusterB = new Cluster();

        assertEquals(0, Cluster.BY_SIZE_COMPARATOR.compare(clusterA, clusterA));
        assertTrue(Cluster.BY_SIZE_COMPARATOR.compare(clusterA, clusterB) > 0);
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

        assertEquals(0, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR.compare(clusterA,
            clusterA));
        assertTrue(Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR.compare(clusterA,
            clusterB) < 0);
        assertTrue(Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR.compare(clusterA,
            clusterC) > 0);
    }
}
