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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import org.carrot2.clustering.Cluster;

@JsonPropertyOrder({"clusters", "serviceInfo"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterResponse {
  @JsonProperty public List<Cluster<Integer>> clusters;

  /** Additional information from the DCS server, if requested. Can be {@code null}. */
  @JsonProperty public ServiceInfo serviceInfo;

  @JsonCreator
  public ClusterResponse(@JsonProperty("clusters") List<Cluster<Integer>> clusters) {
    this.clusters = clusters;
  }
}
