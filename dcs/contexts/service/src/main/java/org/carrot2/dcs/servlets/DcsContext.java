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
package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.dcs.model.ClusterRequest;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.LanguageComponentsLoader;
import org.carrot2.language.LoadedLanguages;
import org.carrot2.util.ResourceLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DcsContext {
  public static final String PARAM_RESOURCES = "resources";
  public static final String PARAM_TEMPLATES = "templates";
  public static final String PARAM_ALGORITHMS = "algorithms";

  private static String KEY = "_dcs_";
  private static Logger console = LoggerFactory.getLogger("console");

  private final LinkedHashMap<String, LanguageComponents> languages;

  final ObjectMapper om;
  final Map<String, ClusterRequest> templates;
  final LinkedHashMap<String, ClusteringAlgorithmProvider> algorithmSuppliers;
  final LinkedHashMap<String, List<String>> algorithmLanguages;
  final ClassLoader cl = this.getClass().getClassLoader();

  private DcsContext(ServletContext servletContext) throws ServletException {
    this.om = new ObjectMapper();
    om.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    Predicate<String> algorithmsFilter;
    String allowedList = servletContext.getInitParameter(PARAM_ALGORITHMS);
    if (allowedList != null && !allowedList.isBlank()) {
      Set<String> allowed = Set.of(allowedList.trim().split("[\\s,]+"));
      algorithmsFilter =
          (algorithm) -> {
            boolean allow = allowed.contains(algorithm);
            if (!allow) {
              console.debug("Algorithm omitted (context filter): {}", algorithm);
            }
            return allow;
          };
    } else {
      algorithmsFilter = (algorithm) -> true;
    }

    this.algorithmSuppliers =
        StreamSupport.stream(
                ServiceLoader.load(ClusteringAlgorithmProvider.class, cl).spliterator(), false)
            .sorted(Comparator.comparing(ClusteringAlgorithmProvider::name))
            .filter(provider -> algorithmsFilter.test(provider.name()))
            .collect(
                Collectors.toMap(
                    e -> e.name(),
                    e -> e,
                    (k1, k2) -> {
                      throw new IllegalStateException("Duplicate algorithm key: " + k1.name());
                    },
                    LinkedHashMap::new));

    this.templates = processTemplates(om, algorithmSuppliers, servletContext);
    this.languages = computeLanguageComponents(algorithmSuppliers, servletContext);

    this.algorithmSuppliers
        .entrySet()
        .removeIf(e -> !isAlgorithmAvailable(e.getValue(), languages.values()));

    this.algorithmLanguages = computeAlgorithmLanguagePairs(algorithmSuppliers, languages.values());

    console.info(
        "DCS context initialized [algorithms: {}, templates: {}]",
        algorithmSuppliers.keySet(),
        templates.keySet());
  }

  private static boolean isAlgorithmAvailable(
      ClusteringAlgorithmProvider provider, Collection<LanguageComponents> languages) {
    ClusteringAlgorithm algorithm = provider.get();
    Optional<LanguageComponents> first = languages.stream().filter(algorithm::supports).findFirst();
    if (first.isEmpty()) {
      console.warn(
          "Algorithm does not support any of the available languages: {}", provider.name());
      return false;
    } else {
      return true;
    }
  }

  private static LinkedHashMap<String, LanguageComponents> computeLanguageComponents(
      LinkedHashMap<String, ClusteringAlgorithmProvider> algorithmSuppliers,
      ServletContext servletContext)
      throws ServletException {
    LanguageComponentsLoader loader = LanguageComponents.loader();

    String resourcePath = servletContext.getInitParameter(PARAM_RESOURCES);
    if (resourcePath != null && !resourcePath.trim().isEmpty()) {
      if (!resourcePath.endsWith("/")) {
        resourcePath += "/";
      }

      console.debug(
          "Will try to load language resources from context path: {}",
          servletContext.getContextPath() + resourcePath);

      ResourceLookup contextLookup = new ServletContextLookup(servletContext, resourcePath);
      loader.withResourceLookup((provider) -> contextLookup);
    }

    // Only load the resources of algorithms we're interested in.
    loader.limitToAlgorithms(
        algorithmSuppliers.values().stream()
            .map(Supplier::get)
            .toArray(ClusteringAlgorithm[]::new));

    LoadedLanguages loadedLanguages;
    try {
      loadedLanguages = loader.load();
    } catch (IOException e) {
      throw new ServletException("Could not load or initialize language resources.", e);
    }

    LinkedHashMap<String, LanguageComponents> languages = new LinkedHashMap<>();
    for (String lang : loadedLanguages.languages()) {
      languages.put(lang, loadedLanguages.language(lang));
    }
    return languages;
  }

  private static LinkedHashMap<String, List<String>> computeAlgorithmLanguagePairs(
      LinkedHashMap<String, ClusteringAlgorithmProvider> algorithmSuppliers,
      Collection<LanguageComponents> languageComponents) {
    LinkedHashMap<String, List<String>> algorithmLanguages = new LinkedHashMap<>();
    for (Map.Entry<String, ClusteringAlgorithmProvider> e : algorithmSuppliers.entrySet()) {
      ClusteringAlgorithm algorithm = e.getValue().get();
      List<String> supportedLanguages =
          languageComponents.stream()
              .filter(lc -> algorithm.supports(lc))
              .map(lc -> lc.language())
              .collect(Collectors.toList());
      algorithmLanguages.put(e.getKey(), supportedLanguages);
      console.debug(
          "Languages supported by algorithm '{}': {}",
          e.getKey(),
          String.join(", ", supportedLanguages));
    }

    return algorithmLanguages;
  }

  public static synchronized DcsContext load(ServletContext servletContext)
      throws ServletException {
    DcsContext context = (DcsContext) servletContext.getAttribute(KEY);
    if (context == null) {
      context = new DcsContext(servletContext);
      servletContext.setAttribute(KEY, context);
    }
    return context;
  }

  private static Map<String, ClusterRequest> processTemplates(
      ObjectMapper om,
      LinkedHashMap<String, ClusteringAlgorithmProvider> algorithmSuppliers,
      ServletContext servletContext)
      throws ServletException {
    String templatePath = servletContext.getInitParameter(PARAM_TEMPLATES);
    if (templatePath == null || templatePath.isEmpty()) {
      console.warn("Template path init parameter is empty.");
      return Collections.emptyMap();
    }

    Map<String, ClusterRequest> templates = new LinkedHashMap<>();
    Set<String> resourcePaths = servletContext.getResourcePaths(templatePath);
    if (resourcePaths != null) {
      for (TemplateInfo ti :
          resourcePaths.stream()
              .filter(path -> path.toLowerCase(Locale.ROOT).endsWith(".json"))
              .map(path -> new TemplateInfo(path))
              .filter(v -> v != null)
              .sorted(Comparator.comparing(t -> t.name))
              .collect(Collectors.toList())) {
        try {
          ClusterRequest requestTemplate =
              om.readValue(servletContext.getResourceAsStream(ti.path), ClusterRequest.class);
          if (requestTemplate.documents != null && !requestTemplate.documents.isEmpty()) {
            console.warn(
                "Templates must not contain the 'documents' property, clearing it in template: {}",
                ti.path);
            requestTemplate.documents = null;
          }

          if (requestTemplate.algorithm != null
              && !algorithmSuppliers.containsKey(requestTemplate.algorithm)) {
            console.debug(
                "Template '{}' omitted because the algorithm is not available: {}",
                ti.path,
                requestTemplate.algorithm);
          } else {
            templates.put(ti.id, requestTemplate);
          }
        } catch (IOException e) {
          throw new ServletException("Could not process request template: " + ti.path, e);
        }
      }
    }
    return templates;
  }

  public LanguageComponents getLanguage(String requestedLanguage) {
    return languages.get(requestedLanguage);
  }

  private static class TemplateInfo {
    private static Pattern NAME_PATTERN =
        Pattern.compile("(/)?(?<name>[^/]+)(.json)$", Pattern.CASE_INSENSITIVE);
    private static Pattern ID_PATTERN =
        Pattern.compile("^(?:[0-9]+)?+(?:\\s*-?\\s*)?+", Pattern.CASE_INSENSITIVE);

    private final String path;
    private final String name;
    private final String id;

    public TemplateInfo(String path) {
      this.path = path;
      Matcher matcher = NAME_PATTERN.matcher(path);
      if (!matcher.find()) {
        throw new RuntimeException("Name part not found?: " + path);
      }
      this.name = matcher.group("name");
      this.id = ID_PATTERN.matcher(name).replaceAll("");
    }
  }
}
