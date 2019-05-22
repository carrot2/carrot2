/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.it;

import java.io.IOException;
import java.net.URI;
import org.carrot2.dcs.examples.E01_DcsClusteringBasics;
import org.junit.Test;

public class DcsExamplesTest extends AbstractDistributionTest {
  @Test
  public void runExamples() throws IOException {
    try (DcsService service = startDcs()) {
      URI dcsService = service.getAddress().resolve("/service/");
      try (E01_DcsClusteringBasics ex = new E01_DcsClusteringBasics(dcsService)) {
        ex.run();
      }
    }
  }
}
