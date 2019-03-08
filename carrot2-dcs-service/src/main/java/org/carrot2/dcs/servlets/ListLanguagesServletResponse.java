package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListLanguagesServletResponse {
  @JsonProperty
  List<String> languages;

  @JsonCreator
  public ListLanguagesServletResponse(@JsonProperty("languages") List<String> languages) {
    this.languages = languages;
  }
}
