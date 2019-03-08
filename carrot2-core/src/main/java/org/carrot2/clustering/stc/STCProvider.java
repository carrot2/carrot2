package org.carrot2.clustering.stc;

import org.carrot2.clustering.ClusteringAlgorithmProvider;

public class STCProvider implements ClusteringAlgorithmProvider {
  @Override
  public String name() {
    return "STC";
  }

  @Override
  public STCClusteringAlgorithm get() {
    return new STCClusteringAlgorithm();
  }
}
