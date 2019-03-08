package org.carrot2.dcs.servlets;

import org.carrot2.language.LanguageComponents;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListLanguagesServlet extends RestEndpoint {

  private ListLanguagesServletResponse defaultResponse;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    defaultResponse = new ListLanguagesServletResponse(defaults());
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    writeJsonResponse(response, shouldIndent(request), defaultResponse);
  }

  static List<String> defaults() {
    return new ArrayList<>(LanguageComponents.languages());
  }
}
