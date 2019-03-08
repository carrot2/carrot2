package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListAlgorithmsServletResponse {
  @JsonProperty
  public List<String> algorithms;

  @JsonCreator
  public ListAlgorithmsServletResponse(@JsonProperty("algorithms") List<String> algorithms) {
    this.algorithms = algorithms;
  }
}
