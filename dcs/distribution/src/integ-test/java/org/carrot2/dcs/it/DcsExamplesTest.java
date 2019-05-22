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
