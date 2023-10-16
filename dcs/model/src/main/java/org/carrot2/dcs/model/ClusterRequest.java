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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/** Models a clustering request for the DCS, serializable to JSON via Jackson. */
@JsonPropertyOrder({
  "language",
  "algorithm",
  "parameters",
  "documents",
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterRequest {
  @JsonProperty public String language;
  @JsonProperty public String algorithm;
  @JsonProperty public Map<String, Object> parameters;
  @JsonProperty public List<Document> documents = new ArrayList<>();

  /** Represents all of document fields for clustering. */
  @JsonDeserialize(converter = Document.FromJsonMap.class)
  @JsonSerialize(converter = Document.ToJsonMap.class)
  public static class Document implements org.carrot2.clustering.Document {
    private Map<String, List<String>> fields = new LinkedHashMap<>();

    public void setField(String field, String value) {
      setField(field, Collections.singletonList(value));
    }

    public void setField(String field, List<String> values) {
      fields.put(field, values);
    }

    @Override
    public void visitFields(BiConsumer<String, String> fieldConsumer) {
      fields.forEach(
          (key, values) -> {
            values.forEach(value -> fieldConsumer.accept(key, value));
          });
    }

    public static class ToJsonMap extends StdConverter<Document, Map<String, Object>> {
      @Override
      public Map<String, Object> convert(Document value) {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        value.fields.forEach(
            (fldName, fldValue) -> {
              out.put(fldName, fldValue.size() == 1 ? fldValue.get(0) : fldValue);
            });
        return out;
      }
    }

    public static class FromJsonMap extends StdConverter<Map<String, Object>, Document> {
      @Override
      public Document convert(Map<String, Object> value) {
        Document doc = new Document();
        value.forEach((fldName, fldValue) -> setField(doc, fldName, fldValue));
        return doc;
      }

      private void setField(Document doc, String field, Object value) {
        if (value instanceof String) {
          doc.setField(field, (String) value);
          return;
        } else if (value instanceof String[]) {
          doc.setField(field, Arrays.asList((String[]) value));
          return;
        } else if (value instanceof List) {
          List<?> asList = (List<?>) value;
          if (asList.stream().allMatch(v -> v instanceof String)) {
            @SuppressWarnings("unchecked")
            List<String> asStringList = (List<String>) asList;
            doc.setField(field, asStringList);
            return;
          }
        }

        throw new IllegalArgumentException(
            "Document's field values can be of type String, String[] or a List<String>: "
                + " field="
                + field
                + ", value="
                + value.getClass().getName());
      }
    }
  }
}
