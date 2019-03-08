package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.dcs.client.ClusterRequest;
import org.carrot2.language.LanguageComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class DcsContext {
  public static final String PATH_TEMPLATES = "/templates";

  private static String KEY = "_dcs_";
  private static Logger log = LoggerFactory.getLogger(DcsContext.class);

  final ObjectMapper om;
  final Map<String, ClusterRequest> templates;
  final Map<String, ClusteringAlgorithmProvider> algorithmSuppliers;
  final Map<String, LanguageComponents> languages;

  private DcsContext(ServletContext servletContext) throws ServletException {
    this.om = new ObjectMapper();
    om.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    this.templates = processTemplates(om, servletContext);
    this.algorithmSuppliers =
        StreamSupport.stream(ServiceLoader.load(ClusteringAlgorithmProvider.class).spliterator(), false)
            .collect(Collectors.toMap(e -> e.name(), e -> e));

    // TODO: Apply custom resource location customizations.
    this.languages = LanguageComponents.languages().stream()
        .collect(Collectors.toMap(
            e -> e,
            e -> LanguageComponents.load(e)));
  }

  public synchronized static DcsContext load(ServletContext servletContext) throws ServletException {
    DcsContext context = (DcsContext) servletContext.getAttribute(KEY);
    if (context == null) {
      context = new DcsContext(servletContext);
      servletContext.setAttribute(KEY, context);
    }
    return context;
  }

  private Map<String, ClusterRequest> processTemplates(ObjectMapper om, ServletContext servletContext) throws ServletException {
    Map<String, ClusterRequest> templates = new LinkedHashMap<>();
    Set<String> resourcePaths = servletContext.getResourcePaths(PATH_TEMPLATES);
    if (resourcePaths != null) {
      resourcePaths = new TreeSet<>(resourcePaths);
      for (String template : resourcePaths) {
        if (template.toLowerCase(Locale.ROOT).endsWith(".json")) {
          try {
            ClusterRequest requestTemplate =
                om.readValue(servletContext.getResourceAsStream(template), ClusterRequest.class);
            if (requestTemplate.documents != null && !requestTemplate.documents.isEmpty()) {
              log.warn("Templates should not contain any documents, but this template does: {}", template);
              requestTemplate.documents = null;
            }
            String id = template.substring(0, template.lastIndexOf('.'));
            id = id.substring(id.lastIndexOf('/') + 1);
            templates.put(id, requestTemplate);
          } catch (IOException e) {
            throw new ServletException("Could not process request template: " + template);
          }
        } else {
          log.info("Ignoring non-template file: {}", template);
        }
      }
    }
    return templates;
  }
}
