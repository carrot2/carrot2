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

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.carrot2.dcs.client.ListResponse;

public class ListServlet extends RestEndpoint {
  private DcsContext dcsContext;
  private ListResponse defaultResponse;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    dcsContext = DcsContext.load(config.getServletContext());
    defaultResponse =
        new ListResponse(
            dcsContext.algorithmLanguages, new ArrayList<>(dcsContext.templates.keySet()));
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    writeJsonResponse(response, shouldIndent(request), defaultResponse);
  }
}
