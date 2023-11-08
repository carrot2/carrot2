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
package org.carrot2.dcs.it;

import java.io.IOException;
import java.net.URI;
import org.carrot2.dcs.JettyContainer;
import org.eclipse.jetty.util.resource.Resource;

public class EmbeddedDcs implements DcsService {
  private final JettyContainer container;
  private final URI serviceUri;

  public EmbeddedDcs(DcsConfig config) throws IOException {
    // Disable JAR caches, otherwise we get locked files on Windows.
    Resource.setDefaultUseCaches(false);

    if (config.enableTestServlet) {
      System.setProperty(SYSPROP_TESTSERVLET_ENABLE, "true");
    }

    if (config.pidFile != null) {
      throw new AssertionError("Can't run with pid file option on embedded DCS.");
    }

    container =
        new JettyContainer(
            0,
            null,
            config.distributionDir.resolve("web"),
            config.shutdownToken,
            config.maxThreads,
            config.useGzip,
            null /* idle time */);

    try {
      container.start();
      serviceUri = URI.create("http://localhost:" + container.getPort());
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  @Override
  public URI getAddress() {
    return serviceUri;
  }

  @Override
  public boolean isRunning() {
    return container.isRunning();
  }

  @Override
  public void close() throws IOException {
    try {
      this.container.stop();
      this.container.join();
      System.clearProperty(SYSPROP_TESTSERVLET_ENABLE);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }
}
