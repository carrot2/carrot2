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
package org.carrot2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChainedResourceLookup implements ResourceLookup {
  private List<ResourceLookup> rls;

  public ChainedResourceLookup(List<ResourceLookup> resourceLookups) {
    rls = new ArrayList<>(resourceLookups);
    if (rls.isEmpty()) {
      throw new IllegalArgumentException("At least one chained lookup is required.");
    }
  }

  @Override
  public InputStream open(String resource) throws IOException {
    ResourceLookup rl = loaderOf(resource);
    if (rl == null) {
      throw new IOException("Resource does not exist: " + pathOf(resource));
    }
    return rl.open(resource);
  }

  @Override
  public boolean exists(String resource) {
    return loaderOf(resource) != null;
  }

  @Override
  public String pathOf(String resource) {
    ResourceLookup rl = loaderOf(resource);
    if (rl != null) {
      return rl.pathOf(resource);
    } else {
      return rls.stream().map(loader -> loader.pathOf(resource)).collect(Collectors.joining(", "));
    }
  }

  private ResourceLookup loaderOf(String resource) {
    for (ResourceLookup rl : rls) {
      if (rl.exists(resource)) {
        return rl;
      }
    }
    return null;
  }
}
