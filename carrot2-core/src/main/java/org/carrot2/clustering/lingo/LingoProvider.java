package org.carrot2.clustering.lingo;

import org.carrot2.clustering.ClusteringAlgorithmProvider;

public class LingoProvider implements ClusteringAlgorithmProvider {
  @Override
  public String name() {
    return "Lingo";
  }

  @Override
  public LingoClusteringAlgorithm get() {
    return new LingoClusteringAlgorithm();
  }
}
