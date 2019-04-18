package org.carrot2.dcs.servlets;

import org.carrot2.util.ResourceLookup;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
    String resourcePath = resourcePath(resource);
    InputStream is = ctx.getResourceAsStream(resourcePath);
    if (is == null) {
      throw new IOException("Resource not found in context: " + resourcePath);
    }
    return is;
  }

  @Override
  public boolean exists(String resource) {
    try {
      return ctx.getResource(resourcePath(resource)) != null;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String pathOf(String resource) {
    try {
      return ctx.getResource(resourcePath(resource)).toExternalForm();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private String resourcePath(String resource) {
    return this.path + resource;
  }
}
