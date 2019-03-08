package org.carrot2.dcs.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListResponse {
  @JsonProperty
  public List<String> algorithms;

  @JsonProperty
  public List<String> languages;

  @JsonProperty
  public List<String> templates;

  @JsonCreator
  public ListResponse(@JsonProperty("algorithms") List<String> algorithms,
                      @JsonProperty("languages") List<String> languages,
                      @JsonProperty("templates") List<String> templates) {
    this.algorithms = algorithms;
    this.languages = languages;
    this.templates = templates;
  }
}
