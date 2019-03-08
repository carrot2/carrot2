package org.carrot2.dcs.servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ListServlet extends RestEndpoint {
  private ListServletResponse defaultResponse;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    defaultResponse = new ListServletResponse(ListAlgorithmsServlet.defaults(), ListLanguagesServlet.defaults());
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    writeJsonResponse(response, shouldIndent(request), defaultResponse);
  }
}
