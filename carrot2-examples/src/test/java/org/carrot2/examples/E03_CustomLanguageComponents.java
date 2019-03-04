
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
  public void listAllAvailableLanguages() {
    // Preprocessing components for several languages are provided in the Carrot2 distribution (and in
    // optional JAR libraries named carrot2-lang-*. These languages self-register with LanguageComponents
    // factory and can be enumerated, as shown here:
    System.out.println("Language preprocessing components for the following languages are available:\n  " +
        LanguageComponents.factories().map(factory -> factory.name()).collect(Collectors.joining("\n  ")));
  }

  @Test
  public void customLanguageComponents() {
    Stream<Document> documentStream = ExamplesData.documentStream();

    // The language-specific components required for clustering algorithms are:
    // a tokenizer, a stemmer and a lexical resource provider. The language components factory
    // lists all languages for which these components are already preconfigured (see the
    // example above).
    //
    // However, if this isn't sufficient or needs to be customized, a custom component set can
    // be assembled, as shown below.
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
    ExamplesCommon.printClusters(clusters, "");
  }
}
