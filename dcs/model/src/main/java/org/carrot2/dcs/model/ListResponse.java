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
package org.carrot2.dcs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"algorithms", "templates"})
public class ListResponse {
  /** A map of algorithm names and their supported languages. */
  @JsonProperty public Map<String, List<String>> algorithms;

  /** A map of template names and their structure (pre-filled {@link ClusterRequest}). */
  @JsonProperty public Map<String, ClusterRequest> templates;

  @JsonCreator
  public ListResponse(
      @JsonProperty("algorithms") Map<String, List<String>> algorithms,
      @JsonProperty("templates") Map<String, ClusterRequest> templates) {
    this.algorithms = algorithms;
    this.templates = templates;
  }

  public static Map<String, ClusterRequest> filterSensitiveDataFromTemplates(
      Map<String, ClusterRequest> templates) {
    Map<String, ClusterRequest> templateInfos = new LinkedHashMap<>();
    templates.forEach(
        (templateName, requestTemplate) -> {
          ClusterRequest templateCopy = new ClusterRequest();
          templateCopy.algorithm = requestTemplate.algorithm;
          templateCopy.language = requestTemplate.language;
          // Don't return any algorithm-specific parameters that the template may
          // have. This may be a security concern if parameters allow something like
          // local paths or credentials.
          templateCopy.documents = null;
          templateCopy.parameters = null;
          templateInfos.put(templateName, templateCopy);
        });
    return templateInfos;
  }
}
