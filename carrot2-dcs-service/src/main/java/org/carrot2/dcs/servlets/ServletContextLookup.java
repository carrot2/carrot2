package org.carrot2.dcs.servlets;

import org.carrot2.util.ResourceLookup;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

class ServletContextLookup implements ResourceLookup {
  private final ServletContext ctx;
  private final String path;

  ServletContextLookup(ServletContext servletContext, String path) {
    this.ctx = servletContext;
    this.path = Objects.requireNonNull(path);
  }

  @Override
  public InputStream open(String resource) throws IOException {
    String resourcePath = this.path + resource;
    InputStream is = ctx.getResourceAsStream(resourcePath);
    if (is == null) {
      throw new IOException("Resource not found in context: " + resourcePath);
    }
    return is;
  }
}
