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
package org.carrot2.clustering.stc;

import org.carrot2.clustering.ClusteringAlgorithmProvider;

public class STCProvider implements ClusteringAlgorithmProvider {
  @Override
  public String name() {
    return STCClusteringAlgorithm.NAME;
  }

  @Override
  public STCClusteringAlgorithm get() {
    return new STCClusteringAlgorithm();
  }
}
