package org.carrot2.dcs.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({
    "algorithm",
    "languages",
    "templates"
})
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
