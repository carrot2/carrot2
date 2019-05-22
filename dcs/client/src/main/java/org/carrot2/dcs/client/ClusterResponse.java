package org.carrot2.dcs.client;

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
