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
package org.carrot2.infra.docattrs;

import com.carrotsearch.jsondoclet.model.JavaDocs;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.carrot2.attrs.AcceptingVisitor;

@JsonPropertyOrder({"name", "type", "javadoc"})
public class ClassInfo {
  transient Class<? extends AcceptingVisitor> clazz;

  @JsonProperty public String name;
  @JsonProperty public String type;
  @JsonProperty public Map<String, AttrInfo> attributes = new LinkedHashMap<>();

  // Imported from the doclet-generated data model.

  @JsonProperty public JavaDocs javadoc;
}
