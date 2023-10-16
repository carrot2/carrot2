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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.language.LanguageComponents;
import org.carrot2.math.matrix.FactorizationQuality;
import org.carrot2.math.matrix.LocalNonnegativeMatrixFactorizationFactory;
import org.junit.Test;

/** This example shows how to tweak clustering algorithm parameters, prior to clustering. */
public class E02_TweakingParameters {
  @Test
  public void tweakLingo() throws IOException {
    LanguageComponents languageComponents = LanguageComponents.loader().load().language("English");

    // Tweak Lingo's defaults. Note each attribute comes with JavaDoc documentation
    // and some are constrained to a specific range of values. Also, each algorithm
    // will typically have a different set of attributes to choose from.
    // fragment-start{parameters}
    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.desiredClusterCount.set(10);
    algorithm.preprocessing.wordDfThreshold.set(5);
    algorithm.preprocessing.phraseDfThreshold.set(5);
    algorithm.preprocessing.documentAssigner.minClusterSize.set(4);
    // fragment-end{parameters}

    // For attributes that are interfaces, provide concrete implementations of that
    // interface, configuring it separately. Programming editors provide support for listing
    // all interface implementations, use it to inspect the possibilities.
    // fragment-start{complex-parameters}
    var factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
    factorizationFactory.factorizationQuality.set(FactorizationQuality.HIGH);

    algorithm.matrixReducer.factorizationFactory = factorizationFactory;
    // fragment-end{complex-parameters}

    List<Cluster<Document>> clusters =
        algorithm.cluster(ExamplesData.documentStream(), languageComponents);
    System.out.println("Clusters from Lingo:");
    ExamplesCommon.printClusters(clusters);
  }

  @Test
  public void tweakStc() throws IOException {
    LanguageComponents languageComponents = LanguageComponents.loader().load().language("English");

    // Tweak Lingo's defaults. Note each attribute comes with JavaDoc documentation
    // and some are constrained to a specific range of values. Also, each algorithm
    // will typically have a different set of attributes to choose from.
    STCClusteringAlgorithm algorithm = new STCClusteringAlgorithm();
    algorithm.maxClusters.set(10);
    algorithm.ignoreWordIfInHigherDocsPercent.set(.8);
    algorithm.preprocessing.wordDfThreshold.set(5);

    List<Cluster<Document>> clusters =
        algorithm.cluster(ExamplesData.documentStream(), languageComponents);
    System.out.println("Clusters from STC:");
    ExamplesCommon.printClusters(clusters);
  }

  @Test
  public void listAllAttributesToJson() {
    Stream.of(
            new LingoClusteringAlgorithm(),
            new STCClusteringAlgorithm(),
            new BisectingKMeansClusteringAlgorithm())
        .forEachOrdered(
            algorithm -> {
              System.out.printf(
                  Locale.ROOT,
                  "\n# Attributes of %s\n%s",
                  algorithm.getClass().getSimpleName(),
                  Attrs.toJson(algorithm, AliasMapper.SPI_DEFAULTS));
            });
  }
}
