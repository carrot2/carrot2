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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.icu.segmentation.DefaultICUTokenizerConfig;
import org.apache.lucene.analysis.icu.segmentation.ICUTokenizer;
import org.apache.lucene.util.AttributeFactory;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.language.DefaultDictionaryImpl;
import org.carrot2.language.LabelFilter;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.LanguageComponentsProvider;
import org.carrot2.language.Stemmer;
import org.carrot2.language.StopwordFilter;
import org.carrot2.language.Tokenizer;
import org.carrot2.language.extras.LuceneAnalyzerTokenizerAdapter;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.ClassRelativeResourceLookup;
import org.junit.Test;

/** This example shows how to tweak language components prior to clustering. */
public class E03_Languages {
  @Test
  public void listAllAvailableLanguages() throws IOException {
    // Preprocessing components for several languages are provided in the Carrot2 distribution (and
    // in optional JAR libraries named carrot2-lang-*. These languages self-register with
    // LanguageComponents factory and can be enumerated, as shown here:

    // fragment-start{language-enumeration}
    System.out.println(
        "Language components for the following languages are available:\n  "
            + String.join(", ", LanguageComponents.loader().load().languages()));
    // fragment-end{language-enumeration}
  }

  @Test
  public void listAllAvailableComponents() throws IOException {
    // List all available languages and their provided components (interfaces).
    // fragment-start{component-enumeration}
    ServiceLoader<LanguageComponentsProvider> providers =
        ServiceLoader.load(LanguageComponentsProvider.class);

    Map<String, List<LanguageComponentsProvider>> langToProviders = new TreeMap<>();
    for (LanguageComponentsProvider prov : providers) {
      for (String lang : prov.languages()) {
        langToProviders.computeIfAbsent(lang, (k) -> new ArrayList<>()).add(prov);
      }
    }

    langToProviders.forEach(
        (language, provList) -> {
          System.out.println("  > " + language);
          provList.forEach(
              provider -> {
                System.out.println("    [Provider: " + provider.name() + "]");
                for (Class<?> componentClass : provider.componentTypes()) {
                  System.out.println("      Component: " + componentClass.getName());
                }
              });
        });
    // fragment-end{component-enumeration}
  }

  @Test
  public void tweakDefaultEnglishResources() throws IOException {
    // Sometimes the default resources are not sufficient or need to be tuned.
    // fragment-start{custom-english-resources}
    LanguageComponents custom =
        LanguageComponents.loader()
            // Note we restrict languages to just English because resources for
            // other languages are missing from the location of resource lookup
            // and would have caused an exception.
            .limitToLanguages("English")
            // and we substitute resource lookup locations with our custom location.
            .withResourceLookup(provider -> new ClassRelativeResourceLookup(E03_Languages.class))
            .load()
            .language("English");
    // fragment-end{custom-english-resources}

    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.desiredClusterCount.set(10);
    List<Cluster<Document>> clusters = algorithm.cluster(ExamplesData.documentStream(), custom);
    System.out.println("Clusters:");
    ExamplesCommon.printClusters(clusters);
  }

  @Test
  public void useEphemeralDictionaries() throws IOException {
    // It is often the case that clustering should be run against
    // temporary, ephemeral lexical data. In this example we will supply such resources
    // directly to the algorithm. Please note that there is a non-zero cost to compile
    // ephemeral dictionaries for each clustering call. If these
    // resources remain static, the LanguageComponents object should be overridden or modified
    // instead.

    // fragment-start{use-ephemeral-dictionary}
    // Load the default dictionaries for English.
    LanguageComponents english =
        LanguageComponents.loader()
            .limitToLanguages("English")
            .limitToAlgorithms(new LingoClusteringAlgorithm())
            .load()
            .language("English");

    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();

    // Create an ephemeral label filter by providing a dictionary with a
    // few regexp exclusion patterns.
    DefaultDictionaryImpl labelFilter = new DefaultDictionaryImpl();
    labelFilter.regexp.set("(?i).*data.*", "(?i).*mining.*");
    algorithm.dictionaries.labelFilters.set(List.of(labelFilter));
    // fragment-end{use-ephemeral-dictionary}

    algorithm.desiredClusterCount.set(10);
    List<Cluster<Document>> clusters = algorithm.cluster(ExamplesData.documentStream(), english);
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

    // We override the suppliers of stemming, stop word filtering and label filtering interfaces.
    // These suppliers must be thread-safe, but the instances of corresponding components will not
    // be reused across threads.

    // fragment-start{custom-stemmer}
    Supplier<Stemmer> stemmerSupplier;
    stemmerSupplier = () -> (word) -> word.toString().toLowerCase(Locale.ROOT);
    // fragment-end{custom-stemmer}

    // fragment-start{custom-lexical-data}
    // Ignore words from the list and anything shorter than 4 characters.
    final Set<String> ignored = new HashSet<>(Arrays.asList("from", "what"));
    final StopwordFilter wordFilter =
        (word) -> {
          // Ignore any word shorter than 4 characters or on the explicit exclusion list.
          return word.length() >= 4 && !ignored.contains(word.toString());
        };

    final LabelFilter labelFilter =
        (label) -> {
          // Ignore any label that has a substring 'data' in it.
          return !label.toString().toLowerCase(Locale.ROOT).contains("data");
        };
    // fragment-end{custom-lexical-data}

    // fragment-start{custom-overrides}
    LanguageComponents customized =
        LanguageComponents.loader()
            .load()
            .language("English")
            .override(Stemmer.class, stemmerSupplier)
            // Word and label filters are thread-safe here so we
            // supply the same instance all the time.
            .override(StopwordFilter.class, () -> wordFilter)
            .override(LabelFilter.class, () -> labelFilter);
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

    final Set<String> ignored = new HashSet<>(Arrays.asList("from", "what"));
    final StopwordFilter wordFilter =
        (word) -> {
          return word.length() > 3 && !ignored.contains(word.toString());
        };
    suppliers.put(StopwordFilter.class, () -> wordFilter);

    final LabelFilter labelFilter =
        (label) -> {
          // Ignore any label that has a substring 'data' in it.
          return !label.toString().toLowerCase(Locale.ROOT).contains("data");
        };
    suppliers.put(LabelFilter.class, () -> labelFilter);

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
