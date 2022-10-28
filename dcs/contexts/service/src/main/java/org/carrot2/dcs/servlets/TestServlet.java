/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2022, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.servlets;

import java.io.IOException;
import java.time.Instant;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TestServlet extends RestEndpoint {
  public static final String SYSPROP_ENABLE = "testservlet.enable";

  private boolean enabled;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    enabled = Boolean.parseBoolean(System.getProperty(SYSPROP_ENABLE, "false"));
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (!enabled) {
      response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      return;
    }

    try {
      if (request.getParameter("sleep") != null) {
        String from = Instant.now().toString();
        Thread.sleep(Long.parseLong(request.getParameter("sleep")));
        String to = Instant.now().toString();
        response.getWriter().println("Slept between: " + from + " - " + to);
      }
    } catch (InterruptedException e) {
      throw new ServletException(e);
    }
  }
}
