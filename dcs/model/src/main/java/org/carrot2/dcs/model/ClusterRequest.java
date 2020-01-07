/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({
  "language",
  "algorithm",
  "parameters",
  "documents",
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterRequest {
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
  }

  @JsonProperty public String language;

  @JsonProperty public String algorithm;

  @JsonProperty public Map<String, Object> parameters;

  @JsonProperty public List<Document> documents = new ArrayList<>();
}
