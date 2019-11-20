/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.infra.docattrs;

import com.carrotsearch.jsondoclet.model.JavaDocs;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;
import org.carrot2.attrs.Attr;

@JsonPropertyOrder({
  "description",
  "type",
  "value",
  "constraints",
  "path",
  "javadoc",
  "implementations"
})
@JsonInclude(Include.NON_NULL)
public class AttrInfo {
  transient Attr<?> attr;

  @JsonProperty public String description;
  @JsonProperty public String type;
  @JsonProperty public Object value;

  @JsonProperty public List<String> constraints;
  @JsonProperty public String path;

  @JsonProperty public Map<String, ClassInfo> implementations;

  // Imported from the doclet-generated data model.

  @JsonProperty public JavaDocs javadoc;
}
