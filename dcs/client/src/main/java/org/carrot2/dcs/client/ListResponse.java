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
package org.carrot2.dcs.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"algorithms", "templates"})
public class ListResponse {
  /** A map of algorithm names and their supported languages. */
  @JsonProperty public Map<String, List<String>> algorithms;

  @JsonProperty public List<String> templates;

  @JsonCreator
  public ListResponse(
      @JsonProperty("algorithms") Map<String, List<String>> algorithms,
      @JsonProperty("templates") List<String> templates) {
    this.algorithms = algorithms;
    this.templates = templates;
  }
}
