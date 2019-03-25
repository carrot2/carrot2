
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples;

import org.carrot2.attrs.*;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.language.LanguageComponents;
import org.carrot2.math.mahout.Arrays;
import org.carrot2.math.matrix.FactorizationQuality;
import org.carrot2.math.matrix.LocalNonnegativeMatrixFactorizationFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * This example shows how to tweak clustering algorithm parameters, prior to clustering.
 */
public class E02_TweakingAttributes {
  @Test
  public void tweakLingo() throws IOException {
    LanguageComponents languageComponents = LanguageComponents.load("English");

    // Tweak Lingo's defaults. Note each attribute comes with JavaDoc documentation
    // and some are contrained to a specific range of values. Also, each algorithm
    // will typically have a different set of attributes to choose from.
    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.desiredClusterCount.set(10);
    algorithm.preprocessing.wordDfThreshold.set(5);
    algorithm.preprocessing.phraseDfThreshold.set(5);
    algorithm.preprocessing.documentAssigner.minClusterSize.set(4);

    // For attributes that are interfaces, provide concrete implementations of that
    // interface, configuring it separately. Programming editors provide support for listing
    // all interface implementations, use it to inspect the possibilities.
    LocalNonnegativeMatrixFactorizationFactory factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
    factorizationFactory.factorizationQuality.set(FactorizationQuality.HIGH);
    algorithm.matrixReducer.factorizationFactory = factorizationFactory;

    List<Cluster<Document>> clusters = algorithm.cluster(ExamplesData.documentStream(), languageComponents);
    System.out.println("Clusters from Lingo:");
    ExamplesCommon.printClusters(clusters);
  }

  @Test
  public void tweakStc() throws IOException {
    LanguageComponents languageComponents = LanguageComponents.load("English");

    // Tweak Lingo's defaults. Note each attribute comes with JavaDoc documentation
    // and some are contrained to a specific range of values. Also, each algorithm
    // will typically have a different set of attributes to choose from.
    STCClusteringAlgorithm algorithm = new STCClusteringAlgorithm();
    algorithm.maxClusters.set(10);
    algorithm.ignoreWordIfInHigherDocsPercent.set(.8);
    algorithm.preprocessing.wordDfThreshold.set(5);

    List<Cluster<Document>> clusters = algorithm.cluster(ExamplesData.documentStream(), languageComponents);
    System.out.println("Clusters from STC:");
    ExamplesCommon.printClusters(clusters);
  }

  @Test
  public void listAllAttributes() {
    // All algorithms implement the visitor pattern so that their (and their default
    // components') attributes can be listed and inspected. For example.
    class Lister implements AttrVisitor {
      private final String lead;

      public Lister(String lead) {
        this.lead = lead;
      }

      @Override
      public void visit(String key, AttrBoolean attr) {
        print(key, attr.get(), "bool", attr);
      }

      @Override
      public void visit(String key, AttrInteger attr) {
        print(key, attr.get(), "int", attr);
      }

      @Override
      public void visit(String key, AttrDouble attr) {
        print(key, attr.get(), "double", attr);
      }

      @Override
      public void visit(String key, AttrEnum<? extends Enum<?>> attr) {
        print(key, attr.get(), "enum of: " + attr.enumClass().getSimpleName(), attr);
      }

      @Override
      public void visit(String key, AttrString attr) {
        print(key, attr.get(), "string", attr);
      }

      @Override
      public void visit(String key, AttrStringArray attr) {
        print(key, Arrays.toString(attr.get()), "array of strings", attr);
      }

      @Override
      public void visit(String key, AttrObject<?> attr) {
        AcceptingVisitor value = attr.get();
        print(key, value == null ? "null" : value.getClass().getSimpleName(),
            "<" + attr.getInterfaceClass().getSimpleName() + ">", attr);
        if (value != null) {
          value.accept(new Lister(lead + key + "."));
        }
      }

      @Override
      public void visit(String key, AttrObjectArray<?> attr) {
        List<? extends AcceptingVisitor> value = attr.get();
        print(key, value == null ? "null" : "list[" + value.size() + "]",
            "array of <" + attr.getInterfaceClass().getSimpleName() + ">", attr);
        if (value != null) {
          for (AcceptingVisitor v : value) {
            v.accept(new Lister(lead + key + "[]."));
          }
        }
      }

      private void print(String key, Object value, String type, Attr<?> attr) {
        System.out.println(String.format(Locale.ROOT,
            "%s%s = %s (%s, %s)",
            lead, key, value, type, attr.getDescription() == null ? "--" : attr.getDescription()));
      }
    }

    Stream.of(
      new LingoClusteringAlgorithm(),
      new STCClusteringAlgorithm(),
      new BisectingKMeansClusteringAlgorithm())
        .forEachOrdered(algorithm -> {
          System.out.println("\n# Attributes of " + algorithm.getClass().getSimpleName());
          algorithm.accept(new Lister(""));
        });
  }
}
