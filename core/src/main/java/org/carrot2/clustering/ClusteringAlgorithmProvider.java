package org.carrot2.clustering;

import java.util.function.Supplier;

public interface ClusteringAlgorithmProvider extends Supplier<ClusteringAlgorithm> {
  String name();
}
