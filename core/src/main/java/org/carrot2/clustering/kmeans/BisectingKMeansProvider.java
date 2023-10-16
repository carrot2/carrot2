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
package org.carrot2.clustering.kmeans;

import org.carrot2.clustering.ClusteringAlgorithmProvider;

public class BisectingKMeansProvider implements ClusteringAlgorithmProvider {
  @Override
  public String name() {
    return BisectingKMeansClusteringAlgorithm.NAME;
  }

  @Override
  public BisectingKMeansClusteringAlgorithm get() {
    return new BisectingKMeansClusteringAlgorithm();
  }
}
