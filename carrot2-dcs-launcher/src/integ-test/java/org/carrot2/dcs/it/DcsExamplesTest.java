package org.carrot2.dcs.it;

import org.carrot2.dcs.examples.E01_DcsClusteringBasics;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

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
