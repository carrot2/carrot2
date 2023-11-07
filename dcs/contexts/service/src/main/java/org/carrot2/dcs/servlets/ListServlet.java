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
package org.carrot2.dcs.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.carrot2.dcs.model.ListResponse;

@SuppressWarnings("serial")
public class ListServlet extends RestEndpoint {
  private DcsContext dcsContext;
  private ListResponse constantResponse;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    dcsContext = DcsContext.load(config.getServletContext());

    constantResponse =
        new ListResponse(
            dcsContext.algorithmLanguages,
            ListResponse.filterSensitiveDataFromTemplates(dcsContext.templates));
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    writeJsonResponse(response, shouldIndent(request), constantResponse);
  }
}
