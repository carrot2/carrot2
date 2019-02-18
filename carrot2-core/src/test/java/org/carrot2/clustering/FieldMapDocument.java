package org.carrot2.clustering;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

public class FieldMapDocument implements Document {
  private final LinkedHashMap<String, String> fields = new LinkedHashMap<>();

  @Override
  public void visitFields(BiConsumer<String, String> fieldConsumer) {
    fields.forEach(fieldConsumer);
  }

  public void addField(String field, String value) {
    if (fields.containsKey(field)) {
      throw new RuntimeException("Non-unique key: " + field);
    }
    fields.put(field, value);
  }
}
