package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class ListServletResponse {
  @JsonProperty
  public List<String> algorithms;

  @JsonProperty
  public List<String> languages;

  @JsonCreator
  public ListServletResponse(@JsonProperty("algorithms") List<String> algorithms,
                             @JsonProperty("languages") List<String> languages) {
    this.algorithms = algorithms;
    this.languages = languages;
  }
}
