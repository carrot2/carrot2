package org.carrot2.clustering;

import java.util.function.Supplier;

public interface ClusteringAlgorithmProvider<T extends ClusteringAlgorithm> extends Supplier<T> {
  String name();
}
