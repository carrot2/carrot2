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

    @Test()
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
        assertThat(d1.id).isEqualTo(2);
        assertThat(d2.id).isEqualTo(6);
        assertThat(d3.id).isEqualTo(7);
        assertThat(d4.id).isEqualTo(5);
        assertThat(d5.id).isEqualTo(8);
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
}
