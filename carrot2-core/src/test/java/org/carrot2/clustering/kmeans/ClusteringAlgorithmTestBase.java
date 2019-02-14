package org.carrot2.clustering.kmeans;

import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.Nightly;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.test.SampleDocumentData;
import org.carrot2.core.test.assertions.Carrot2CoreAssertions;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ClusteringAlgorithmTestBase extends CarrotTestCase {
  protected abstract ClusteringAlgorithm algorithm();

  /**
   * A test to check if the algorithm does not fail with no documents.
   */
  @Test
  public void testNoDocuments() {
    final List<Cluster> clusters = algorithm().cluster(
        Collections.emptyList(),
        TestLanguageModel.createNew());

    assertThat(clusters).isEmpty();
  }

  @Test
  public void testEmptyDocuments() {
    List<Document> documents = IntStream.range(0, randomIntBetween(1, 100))
        .mapToObj(i -> new Document())
        .collect(Collectors.toList());

    final List<Cluster> clusters = algorithm().cluster(
        documents,
        TestLanguageModel.createNew());

    assertThat(clusters).hasSize(1);
    assertThat(clusters.get(0).size()).isEqualTo(documents.size());
  }

  @Test
  public void testClusteringDataMining() {
    final List<Cluster> clusters = algorithm().cluster(
        SampleDocumentData.DOCUMENTS_DATA_MINING,
        TestLanguageModel.createNew());

    assertThat(clusters.size()).isGreaterThan(0);
  }


  /**
   * Runs the algorithm concurrently, verifying stability of results.
   */
  @Nightly
  @Test
  @ThreadLeakLingering(linger = 5000)
  public void testResultsStable() throws Exception {
    final int numberOfThreads = randomIntBetween(1, 10);
    final int queriesPerThread = scaledRandomIntBetween(5, 25);

    List<Document> documents = RandomizedTest.randomFrom(SampleDocumentData.ALL);
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    List<Callable<List<Cluster>>> callables = new ArrayList<>();
    for (int i = 0; i < numberOfThreads * queriesPerThread; i++) {
      final int dataSetIndex = i;
      callables.add(() -> algorithm().cluster(documents, TestLanguageModel.createNew()));
    }

    try {
      List<Cluster> reference = null;
      for (Future<List<Cluster>> f : executorService.invokeAll(callables)) {
        List<Cluster> clusters = f.get();
        if (reference == null) {
          reference = clusters;
        } else {
          Carrot2CoreAssertions.assertThatClusters(clusters).isEquivalentTo(reference);
        }
      }
    } finally {
      executorService.shutdown();
    }
  }
}
