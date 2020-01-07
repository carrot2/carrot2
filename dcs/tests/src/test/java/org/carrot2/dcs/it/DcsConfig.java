/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.it;

import java.nio.file.Path;
import java.util.Objects;

public class DcsConfig {
  final String shutdownToken;
  final Path distributionDir;

  Integer maxThreads;

  boolean enableTestServlet;

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
}
