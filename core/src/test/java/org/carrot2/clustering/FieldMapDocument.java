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
package org.carrot2.clustering;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

/** An implementation of {@link Document} that stores explicit key-value fields. */
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
