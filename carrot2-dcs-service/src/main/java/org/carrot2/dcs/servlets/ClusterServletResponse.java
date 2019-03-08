package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.carrot2.clustering.Cluster;

import java.util.List;

public class ClusterServletResponse {
  @JsonProperty
  List<Cluster<Integer>> clusters;

  @JsonCreator
  public ClusterServletResponse(@JsonProperty("clusters") List<Cluster<Integer>> clusters) {
    this.clusters = clusters;
  }
}
