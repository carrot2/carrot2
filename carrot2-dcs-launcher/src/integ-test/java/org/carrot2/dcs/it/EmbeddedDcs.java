package org.carrot2.dcs.it;

import org.carrot2.dcs.JettyContainer;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public class EmbeddedDcs implements DcsService {
  private final JettyContainer container;
  private final URI serviceUri;

  public EmbeddedDcs(Path distributionDir, String shutdownToken) throws IOException {
    // Disable JAR caches, otherwise we get locked files on Windows.
    Resource.setDefaultUseCaches(false);

    container = new JettyContainer(0, distributionDir.resolve("web"), shutdownToken);
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
    } catch (Exception e) {
      throw new IOException(e);
    }
  }
}
