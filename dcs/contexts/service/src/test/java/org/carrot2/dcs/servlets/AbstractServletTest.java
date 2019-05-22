/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.servlets;

import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.carrot2.TestBase;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServletTest extends TestBase {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Mock protected HttpServletRequest request;

  @Mock protected HttpServletResponse response;

  @Mock protected ServletConfig config;

  @Mock protected ServletContext context;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    when(config.getServletContext()).thenReturn(context);
    when(request.getParameter(ClusterServlet.PARAM_INDENT)).thenReturn("true");
  }

  protected void setupMockTemplates(
      Function<String, InputStream> streamSupplier, String... templates) {
    String templatesPath = "/templates";
    when(context.getInitParameter(DcsContext.PARAM_TEMPLATES)).thenReturn(templatesPath);

    Set<String> set = new LinkedHashSet<>();
    for (String template : templates) {
      String path = templatesPath + "/" + template;
      when(context.getResourceAsStream(path)).thenReturn(streamSupplier.apply(template));
      set.add(path);
    }

    when(context.getResourcePaths(templatesPath)).thenReturn(set);
  }

  protected void setupMockTemplates(String... templates) {
    setupMockTemplates(this::resourceStream, templates);
  }
}
