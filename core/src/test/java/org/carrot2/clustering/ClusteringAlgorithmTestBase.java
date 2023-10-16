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
package org.carrot2.clustering;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.attrs.*;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant1;
import org.junit.Test;

public abstract class ClusteringAlgorithmTestBase<E extends ClusteringAlgorithm & AcceptingVisitor>
    extends TestBase {
  protected abstract E algorithm();

  protected LanguageComponents testLanguageModel() {
    return CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant1.NAME);
  }

  @Test
  public void testExtractPopulate() {
    E algorithm = algorithm();
    Map<String, Object> extracted = Attrs.extract(algorithm);
    Attrs.populate(algorithm, extracted);
  }

  @Test
  public void ensureAllDefaultAttrsHaveRegisteredAliases() {
    // Run serialization with Alias mapper defaults.
    Map<String, Object> asMap = Attrs.toMap(algorithm(), AliasMapper.SPI_DEFAULTS::toName);

    AcceptingVisitor reconstructed =
        Attrs.fromMap(AcceptingVisitor.class, asMap, AliasMapper.SPI_DEFAULTS::fromName);

    Assertions.assertThat(reconstructed).isNotNull().isInstanceOf(algorithm().getClass());
  }

  @Test
  public void ensureAttributesHaveDescriptions() {
    ArrayList<String> errors = new ArrayList<>();
    algorithm()
        .accept(
            new AttrVisitor() {
              ArrayDeque<String> path = new ArrayDeque<>();

              @Override
              public void visit(String key, AttrBoolean attr) {
                hasLabel(key, attr);
              }

              @Override
              public void visit(String key, AttrInteger attr) {
                hasLabel(key, attr);
              }

              @Override
              public void visit(String key, AttrDouble attr) {
                hasLabel(key, attr);
              }

              @Override
              public <T extends Enum<T>> void visit(String key, AttrEnum<T> attr) {
                hasLabel(key, attr);
              }

              @Override
              public void visit(String key, AttrString attr) {
                hasLabel(key, attr);
              }

              @Override
              public void visit(String key, AttrStringArray attr) {
                hasLabel(key, attr);
              }

              @Override
              public <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr) {
                hasLabel(key, attr);
                AcceptingVisitor o = attr.get();
                if (o != null) {
                  path.addLast(key);
                  o.accept(this);
                  path.removeLast();
                }
              }

              @Override
              public <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr) {
                hasLabel(key, attr);

                List<T> entries = attr.get();
                if (entries != null) {
                  entries.forEach(
                      v -> {
                        path.addLast(key);
                        v.accept(this);
                        path.removeLast();
                      });
                }
              }

              private void hasLabel(String key, Attr<?> attr) {
                if (attr.getDescription() == null) {
                  path.addLast(key);
                  errors.add("Attribute has no description: " + String.join(".", path));
                  path.removeLast();
                }
              }
            });

    Assertions.assertThat(errors).isEmpty();
  }

  /** A test to check if the algorithm does not fail with no documents. */
  @Test
  public void testNoDocuments() {
    assertThat(algorithm().cluster(Stream.empty(), testLanguageModel())).isEmpty();
  }

  @Test
  public void testDocumentsWithoutContent() {
    List<Document> documents =
        IntStream.range(0, randomIntBetween(1, 100))
            .mapToObj(
                i ->
                    (Document)
                        fieldConsumer -> {
                          // No fields.
                        })
            .collect(Collectors.toList());

    final List<Cluster<Document>> clusters =
        algorithm().cluster(documents.stream(), testLanguageModel());

    assertThat(clusters).isEmpty();
  }

  @Test
  public void testClusteringSampleDataSet() {
    List<Cluster<Document>> clusters =
        algorithm()
            .cluster(
                SampleDocumentData.DOCUMENTS_DATA_MINING.stream(),
                CachedLangComponents.loadCached("English"));

    assertThat(clusters.size()).isGreaterThan(0);

    for (Cluster<?> c : clusters) {
      System.out.println(c);
    }
  }

  @Test
  public void testAttrGetAndSet() {
    AcceptingVisitor algorithm = algorithm();
    Map<String, Object> map = Attrs.toMap(algorithm, JvmNameMapper.INSTANCE::toName);
    Attrs.fromMap(AcceptingVisitor.class, map, JvmNameMapper.INSTANCE::fromName);

    System.out.println(Attrs.toJson(algorithm, AliasMapper.SPI_DEFAULTS));
  }

  /** Runs the algorithm concurrently, verifying stability of results. */
  @Test
  @ThreadLeakLingering(linger = 5000)
  public void testResultsStableFromSameOrder() throws Exception {
    final int numberOfThreads = randomIntBetween(1, 8);
    final int queriesPerThread = scaledRandomIntBetween(5, 25);

    System.out.println("Threads: " + numberOfThreads + ", qpt: " + queriesPerThread);

    List<Document> documents =
        RandomizedTest.randomFrom(
            Arrays.asList(
                SampleDocumentData.DOCUMENTS_DATA_MINING, SampleDocumentData.DOCUMENTS_DAWID));

    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    List<Callable<List<Cluster<Document>>>> callables = new ArrayList<>();
    for (int i = 0; i < numberOfThreads * queriesPerThread; i++) {
      final int dataSetIndex = i;
      callables.add(
          () -> {
            long s = System.currentTimeMillis();
            try {
              return algorithm().cluster(documents.stream(), testLanguageModel());
            } finally {
              System.out.println(
                  String.format(
                      Locale.ROOT,
                      "Done %s, %.2f sec. ",
                      dataSetIndex,
                      (System.currentTimeMillis() - s) / 1000d));
            }
          });
    }

    try {
      List<Cluster<Document>> reference = null;
      for (Future<List<Cluster<Document>>> f : executorService.invokeAll(callables)) {
        List<Cluster<Document>> clusters = f.get();
        if (reference == null) {
          reference = clusters;
        } else {
          assertThat(clusters).containsExactlyElementsOf(reference);
        }
      }
    } finally {
      executorService.shutdown();
    }
  }

  /** Runs the algorithm concurrently, verifying stability of results. */
  @Test
  @ThreadLeakLingering(linger = 5000)
  public void testResultsStableFromRandomShuffle() throws Exception {
    final int numberOfThreads = randomIntBetween(1, 8);
    final int queriesPerThread = scaledRandomIntBetween(5, 25);

    System.out.println("Threads: " + numberOfThreads + ", qpt: " + queriesPerThread);

    List<Document> documents =
        RandomizedTest.randomFrom(
            Arrays.asList(
                SampleDocumentData.DOCUMENTS_DATA_MINING, SampleDocumentData.DOCUMENTS_DAWID));

    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    List<Callable<List<Cluster<Document>>>> callables = new ArrayList<>();
    for (int i = 0; i < numberOfThreads * queriesPerThread; i++) {
      final int dataSetIndex = i;
      callables.add(
          () -> {
            long s = System.currentTimeMillis();
            try {
              ArrayList<Document> cloned = new ArrayList<>(documents);
              Collections.shuffle(cloned);
              return algorithm().cluster(cloned.stream(), testLanguageModel());
            } finally {
              System.out.println(
                  String.format(
                      Locale.ROOT,
                      "Done %s, %.2f sec. ",
                      dataSetIndex,
                      (System.currentTimeMillis() - s) / 1000d));
            }
          });
    }

    try {
      List<Cluster<Document>> reference = null;
      for (Future<List<Cluster<Document>>> f : executorService.invokeAll(callables)) {
        List<Cluster<Document>> clusters = f.get();
        // Order documents by their hash code so that equality works.
        orderDocsByHash(clusters);
        if (reference == null) {
          reference = clusters;
        } else {
          assertThat(clusters).containsExactlyElementsOf(reference);
        }
      }
    } finally {
      executorService.shutdown();
    }
  }

  private void orderDocsByHash(List<Cluster<Document>> clusters) {
    clusters.forEach(
        c -> {
          c.getDocuments().sort(Comparator.comparingInt(Object::hashCode));
          orderDocsByHash(c.getClusters());
        });
  }

  protected static Stream<String> clusterLabels(List<Cluster<Document>> clusters) {
    return clusters.stream()
        .flatMap(c -> c.getLabels().stream())
        .map(label -> label.toLowerCase(Locale.ROOT));
  }
}
