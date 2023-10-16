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

/** Request processing and service state information (for debugging and diagnostics). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceInfo {
  @JsonProperty public Long clusteringTimeMillis;

  @JsonProperty public Long requestHandlingTimeMillis;
}
