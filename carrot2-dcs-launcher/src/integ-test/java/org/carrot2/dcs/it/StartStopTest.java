package org.carrot2.dcs.it;

import org.junit.Test;

import java.nio.file.Paths;

public class StartStopTest {
  @Test
  public void startStop() {
    System.out.println(Paths.get(".").normalize().toAbsolutePath());
    // TODO: locate assembly build folder. verify it's there.
    // fork the process and wait for jetty to start up.
  }
}
