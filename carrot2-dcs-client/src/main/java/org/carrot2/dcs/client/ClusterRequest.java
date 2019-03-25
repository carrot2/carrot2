package org.carrot2.dcs.client;

import com.fasterxml.jackson.annotation.*;

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

  @JsonProperty
  public String language;

  @JsonProperty
  public String algorithm;

  @JsonProperty
  public Map<String, Object> parameters;

  @JsonProperty
  public List<Document> documents = new ArrayList<>();
}
