package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClusterServletRequest {
  public static class Document {
    private Map<String, String> fields = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, String> getFields() {
      return fields;
    }

    @JsonAnySetter
    public void setField(String field, String value) {
      fields.put(field, value);
    }

    void clear() {
      fields = null;
    }
  }

  @JsonProperty
  List<Document> documents = new ArrayList<>();
}
