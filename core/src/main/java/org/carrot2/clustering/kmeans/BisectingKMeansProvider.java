package org.carrot2.clustering.kmeans;

import org.carrot2.clustering.ClusteringAlgorithmProvider;

public class BisectingKMeansProvider implements ClusteringAlgorithmProvider {
  @Override
  public String name() {
    return "Bisecting K-Means";
  }

  @Override
  public BisectingKMeansClusteringAlgorithm get() {
    return new BisectingKMeansClusteringAlgorithm();
  }
}
