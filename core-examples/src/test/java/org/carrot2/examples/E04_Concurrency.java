/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.examples;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.language.LanguageComponents;
import org.carrot2.math.matrix.NonnegativeMatrixFactorizationKLFactory;
import org.junit.Test;

/** This example shows clustering algorithms in concurrent processing scenarios. */
public class E04_Concurrency {
  @Test
  public void ephemeral() throws Exception {
    // Loading language components can be a heavy operation so it's best to do it once.
    // After language components have been loaded, they can be reused
    // across concurrent threads.
    LanguageComponents english = LanguageComponents.loader().load().language("English");

    // Carrot2 components are *not* designed to be reused concurrently by multiple threads. A single
    // algorithm should only be used by one thread at a time.
    //
    // If clustering is to be performed in parallel, the application needs to
    // provide separate algorithm instances for each thread. The simplest
    // way to achieve thread-safety is to create components on the fly and
    // discard them after the clustering completes.

    // fragment-start{ephemeral}
    Function<Stream<Document>, List<Cluster<Document>>> processor =
        (documentStream) -> {
          // Algorithm instances are created per-call (per-thread)
          LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
          // ...configured in place
          algorithm.preprocessing.phraseDfThreshold.set(10);
          // and discarded once clustering call completes.
          return algorithm.cluster(documentStream, english);
        };

    runConcurrentClustering(processor);
    // fragment-end{ephemeral}
  }

  @Test
  public void cloningVisitor() throws Exception {
    LanguageComponents english = LanguageComponents.loader().load().language("English");

    // Sometimes it may be more convenient to configure an algorithm instance and then create
    // a clone of it for each processor thread. This can be done with the default attribute
    // visitor that converts attributes to a map (and back).

    // fragment-start{cloning}
    // Apply any configuration tweaks once.
    LingoClusteringAlgorithm preconfigured = new LingoClusteringAlgorithm();
    preconfigured.preprocessing.phraseDfThreshold.set(10);
    preconfigured.desiredClusterCount.set(10);
    preconfigured.matrixReducer.factorizationFactory =
        new NonnegativeMatrixFactorizationKLFactory();

    // Populate the map with algorithm and its attributes.
    Map<String, Object> attrs = Attrs.toMap(preconfigured);

    // Reuse the previously populated map to create a new cloned instance.
    Function<Stream<Document>, List<Cluster<Document>>> processor =
        (documentStream) -> {
          ClusteringAlgorithm cloned;
          cloned = Attrs.fromMap(ClusteringAlgorithm.class, attrs);
          return cloned.cluster(documentStream, english);
        };

    runConcurrentClustering(processor);
    // fragment-end{cloning}
  }

  /**
   * This method runs multiple concurrent threads and applies the same clustering function predicate
   * to a randomized document stream.
   */
  private void runConcurrentClustering(
      Function<Stream<Document>, List<Cluster<Document>>> processor)
      throws InterruptedException, ExecutionException {
    // Let's say we have 50 clustering requests to process with all available CPU cores (in the
    // default fork-join pool).
    Collection<Callable<List<Cluster<Document>>>> tasks =
        IntStream.range(0, 50)
            .mapToObj(
                ord ->
                    (Callable<List<Cluster<Document>>>)
                        () -> {
                          // Shuffle input documents for each request.
                          ArrayList<String[]> fieldValues =
                              new ArrayList<>(Arrays.asList(ExamplesData.DOCUMENTS_DATA_MINING));
                          Collections.shuffle(fieldValues, new Random(ord));

                          Stream<Document> documentStream =
                              fieldValues.stream()
                                  .map(
                                      fields ->
                                          (fieldVisitor) -> {
                                            fieldVisitor.accept("title", fields[1]);
                                            fieldVisitor.accept("content", fields[2]);
                                          });

                          long start = System.nanoTime();
                          List<Cluster<Document>> clusters = processor.apply(documentStream);
                          long end = System.nanoTime();
                          System.out.println(
                              String.format(
                                  Locale.ROOT,
                                  "Done clustering request: %d [%.2f sec.], %d cluster(s)",
                                  ord,
                                  (end - start) / (double) TimeUnit.SECONDS.toNanos(1),
                                  clusters.size()));
                          return clusters;
                        })
            .collect(Collectors.toList());

    ExecutorService service = ForkJoinPool.commonPool();
    for (Future<List<Cluster<Document>>> future : service.invokeAll(tasks)) {
      // Consume the output of all tasks.
      future.get();
    }
  }
}
