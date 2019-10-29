/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.examples;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.icu.segmentation.DefaultICUTokenizerConfig;
import org.apache.lucene.analysis.icu.segmentation.ICUTokenizer;
import org.apache.lucene.util.AttributeFactory;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.language.*;
import org.carrot2.language.extras.LuceneAnalyzerTokenizerAdapter;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.ClassRelativeResourceLookup;
import org.carrot2.util.ResourceLookup;
import org.junit.Test;

/** This example shows how to tweak language components prior to clustering. */
public class E03_CustomLanguageComponents {
  @Test
  public void listAllAvailableLanguages() throws IOException {
    // Preprocessing components for several languages are provided in the Carrot2 distribution (and
    // in optional JAR libraries named carrot2-lang-*. These languages self-register with
    // LanguageComponents factory and can be enumerated, as shown here:

    // fragment-start{language-enumeration}
    System.out.println(
        "Language components for the following languages are available:\n  "
            + String.join(", ", LanguageComponents.languages()));
    // fragment-end{language-enumeration}
  }

  @Test
  public void listAllAvailableComponents() throws IOException {
    // List all available languages and their provided components (interfaces).
    // fragment-start{component-enumeration}
    ServiceLoader<LanguageComponentsProvider> providers =
        ServiceLoader.load(LanguageComponentsProvider.class);
    for (LanguageComponentsProvider prov : providers) {
      System.out.println("Provider class: " + prov.name());

      for (String language : prov.languages()) {
        System.out.println("  > " + language);
        for (Class<?> componentClass : prov.load(language).keySet()) {
          System.out.println("    Component: " + componentClass.getName());
        }
      }
    }
    // fragment-end{component-enumeration}
  }

  @Test
  public void tweakDefaultEnglishResources() throws IOException {
    // Sometimes the default resources are not sufficient or need to be tuned.
    // fragment-start{custom-english-resources}
    ResourceLookup resLookup = new ClassRelativeResourceLookup(E03_CustomLanguageComponents.class);
    LanguageComponents custom = LanguageComponents.load("English", resLookup);
    // fragment-end{custom-english-resources}

    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.desiredClusterCount.set(10);
    List<Cluster<Document>> clusters = algorithm.cluster(ExamplesData.documentStream(), custom);
    System.out.println("Clusters:");
    ExamplesCommon.printClusters(clusters);
  }

  @Test
  public void overrideDefaultComponents() throws IOException {
    // There are language-specific components required for clustering and each algorithm may
    // require a different sub-set of these. Typically, algorithms will require
    // a tokenizer, a stemmer and a lexical resource provider.
    //
    // These components can be supplied directly in case of a custom language processing
    // requirements. Here, we modify the stemmer and lexical data for the default English
    // component set, leaving any other components as they were originally defined for English.

    // We override the suppliers of Stemmer and LexicalData interfaces. These suppliers must be
    // thread-safe, but the instances of corresponding components will not be reused across threads.

    // fragment-start{custom-stemmer}
    Supplier<Stemmer> stemmerSupplier;
    stemmerSupplier = () -> (word) -> word.toString().toLowerCase(Locale.ROOT);
    // fragment-end{custom-stemmer}

    // fragment-start{custom-lexical-data}
    final Set<String> ignored = new HashSet<>(Arrays.asList("from", "what"));
    Supplier<LexicalData> lexicalDataSupplier =
        () ->
            new LexicalData() {
              @Override
              public boolean ignoreLabel(CharSequence candidate) {
                // Ignore any label that has a substring 'data' in it.
                return candidate.toString().toLowerCase(Locale.ROOT).contains("data");
              }

              @Override
              public boolean ignoreWord(CharSequence word) {
                return word.length() <= 3 || ignored.contains(word.toString());
              }
            };
    // fragment-end{custom-lexical-data}

    // fragment-start{custom-overrides}
    LanguageComponents customized =
        LanguageComponents.load("English")
            .override(Stemmer.class, stemmerSupplier)
            .override(LexicalData.class, lexicalDataSupplier);
    // fragment-end{custom-overrides}

    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.desiredClusterCount.set(10);
    List<Cluster<Document>> clusters = algorithm.cluster(ExamplesData.documentStream(), customized);
    System.out.println("Clusters:");
    ExamplesCommon.printClusters(clusters);
  }

  @Test
  public void customLanguagePipeline() throws IOException {
    // This example assembles a full set of components for custom preprocessing. Note that
    // typically it's far easier to tweak an existing language's set of components as than
    // build the entire pipeline (as shown in the previous example).

    Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();

    suppliers.put(
        Stemmer.class,
        (Supplier<Stemmer>) () -> ((word) -> word.toString().toLowerCase(Locale.ROOT)));

    suppliers.put(
        LexicalData.class,
        () ->
            new LexicalData() {
              Set<String> ignored = new HashSet<>(Arrays.asList("from", "what"));

              @Override
              public boolean ignoreLabel(CharSequence labelCandidate) {
                // Ignore any label that has a substring 'data' in it; example.
                return labelCandidate.toString().toLowerCase(Locale.ROOT).contains("data");
              }

              @Override
              public boolean ignoreWord(CharSequence word) {
                return word.length() <= 3 || ignored.contains(word.toString());
              }
            });

    // Use an ICU analyzer from Lucene and an adapter to Tokenizer interface.
    class ICUAnalyzer extends Analyzer {
      protected TokenStreamComponents createComponents(String fieldName) {
        return new TokenStreamComponents(
            new ICUTokenizer(
                AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,
                new DefaultICUTokenizerConfig(true, true)));
      }
    }
    suppliers.put(Tokenizer.class, () -> new LuceneAnalyzerTokenizerAdapter(new ICUAnalyzer()));

    suppliers.put(LabelFormatter.class, () -> new LabelFormatterImpl(" "));

    // The assembled language components instance should be reused for subsequent
    // clustering calls.
    LanguageComponents customLanguage = new LanguageComponents("Custom", suppliers);

    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.desiredClusterCount.set(10);
    List<Cluster<Document>> clusters =
        algorithm.cluster(ExamplesData.documentStream(), customLanguage);
    System.out.println("Clusters:");
    ExamplesCommon.printClusters(clusters);
  }
}
