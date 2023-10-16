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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class DcsConfig {
  public final String shutdownToken;
  public final Path distributionDir;

  public Path pidFile;
  public Integer maxThreads;
  public boolean enableTestServlet;
  public boolean useGzip;

  public DcsConfig(Path distributionDir, String shutdownToken) {
    this.shutdownToken = Objects.requireNonNull(shutdownToken);
    this.distributionDir = distributionDir.toAbsolutePath();
  }

  public DcsConfig withMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
    return this;
  }

  public DcsConfig withTestServlet(boolean enableTestServlet) {
    this.enableTestServlet = enableTestServlet;
    return this;
  }

  public DcsConfig withGzip(boolean flag) {
    this.useGzip = flag;
    return this;
  }

  public DcsConfig withPidFile(Path path) throws IOException {
    if (!Files.isDirectory(path.getParent())) {
      throw new IOException("Not a directory: " + path);
    }
    this.pidFile = path;
    return this;
  }
}
