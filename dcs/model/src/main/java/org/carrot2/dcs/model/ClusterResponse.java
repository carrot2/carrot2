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
package org.carrot2.dcs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.carrot2.clustering.Cluster;

public class ClusterResponse {
  @JsonProperty public List<Cluster<Integer>> clusters;

  @JsonCreator
  public ClusterResponse(@JsonProperty("clusters") List<Cluster<Integer>> clusters) {
    this.clusters = clusters;
  }
}
