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
package org.carrot2.clustering;

import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.kmeans.BisectingKMeansProvider;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.lingo.LingoProvider;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.clustering.stc.STCProvider;
import org.junit.Test;

public class AlgorithmProviderNameConsistencyTest extends TestBase {
  @Test
  public void testNamesConsistent() {
    Assertions.assertThat(new LingoProvider().name()).isEqualTo(LingoClusteringAlgorithm.NAME);
    Assertions.assertThat(new STCProvider().name()).isEqualTo(STCClusteringAlgorithm.NAME);
    Assertions.assertThat(new BisectingKMeansProvider().name())
        .isEqualTo(BisectingKMeansClusteringAlgorithm.NAME);
  }
}
