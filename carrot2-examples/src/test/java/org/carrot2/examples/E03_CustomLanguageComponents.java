
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

import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.language.*;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This example shows how to tweak clustering algorithm parameters, prior to clustering.
 */
public class E03_CustomLanguageComponents {
  @Test
  public void customLanguageComponents() {
    Stream<Document> documentStream = ExampleData.documentStream();

    // Language-specific components required for clustering algorithms are: a tokenizer, a stemmer and
    // a lexical resource provider.
    // These components are provided for many languages by default and are available via an enumeration
    // in LanguageComponents, shown here:
    System.out.println("Preprocessing components for the following languages are available:\n  " +
      LanguageComponents.factories().map(factory -> factory.name()).collect(Collectors.joining("\n  ")));

    // However, if this isn't sufficient or needs to be customized, a custom language component set can
    // be assembled, as shown here.
    Stemmer stemmer = (word) -> word.toString().toLowerCase(Locale.ROOT);
    Tokenizer tokenizer = new ExtendedWhitespaceTokenizer();
    LexicalData lexicalData = new LexicalData() {
      Set<String> ignored = new HashSet<>(Arrays.asList(
          "from", "what"
      ));

      @Override
      public boolean ignoreLabel(CharSequence labelCandidate) {
        // Ignore any label that has a substring 'data' in it; example.
        return labelCandidate.toString().toLowerCase(Locale.ROOT).contains("data");
      }

      @Override
      public boolean ignoreWord(CharSequence word) {
        return word.length() <= 3 || ignored.contains(word.toString());
      }
    };
    LanguageComponents languageComponents = new LanguageComponents(stemmer, tokenizer, lexicalData);

    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.desiredClusterCount.set(10);
    List<Cluster<Document>> clusters = algorithm.cluster(documentStream, languageComponents);
    System.out.println("Clusters:");
    ExampleCommon.printClusters(clusters, "");
  }
}
