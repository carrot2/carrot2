
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.lingo;

import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmTestBase;

public class LingoClusteringAlgorithmTest extends ClusteringAlgorithmTestBase {
  @Override
  protected ClusteringAlgorithm algorithm() {
    return new LingoClusteringAlgorithm();
  }
}