package org.carrot2.dcs.servlets;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

public abstract class AbstractServletTest {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Mock
  protected HttpServletRequest request;

  @Mock
  protected HttpServletResponse response;

  @Mock
  protected ServletConfig config;

  @Mock
  protected ServletContext context;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    when(config.getServletContext()).thenReturn(context);
    when(request.getParameter(ClusterServlet.PARAM_INDENT)).thenReturn("true");
  }

  protected void setupMockTemplates(String... templates) {
    String templatesPath = "/templates";
    when(context.getInitParameter(DcsContext.PARAM_TEMPLATES))
        .thenReturn(templatesPath);

    Set<String> set = new LinkedHashSet<>();
    for (String template : templates) {
      String path = templatesPath + "/" + template;
      when(context.getResourceAsStream(path))
          .thenReturn(resourceStream(template));
      set.add(path);
    }

    when(context.getResourcePaths(templatesPath))
        .thenReturn(set);
  }

  protected byte[] resourceBytes(String resource) {
    try (InputStream is = getClass().getResourceAsStream(resource)) {
      if (is == null) {
        throw new RuntimeException("Resource not found: " + resource);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte [] buf = new byte [1024];
      for (int len; (len = is.read(buf)) > 0;) {
        baos.write(buf, 0, len);
      }
      return baos.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected String resourceString(String resource) {
    return new String(resourceBytes(resource), StandardCharsets.UTF_8);
  }

  protected InputStream resourceStream(String resource) {
    return new ByteArrayInputStream(resourceBytes(resource));
  }
}
