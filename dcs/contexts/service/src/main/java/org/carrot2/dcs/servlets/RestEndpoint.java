package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class RestEndpoint extends HttpServlet {
  public static final String PARAM_INDENT = "indent";

  private static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=UTF-8";
  private static final Set<String> YES = new HashSet<>(Arrays.asList("yes", "true", ""));

  private ObjectMapper om;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    om = new ObjectMapper();
  }

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Permit cross-site requests by default.
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader(
        "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST");
    super.service(request, response);
  }

  protected void writeJsonResponse(
      HttpServletResponse response, boolean indent, Object jsonResponse) throws IOException {
    response.setContentType(CONTENT_TYPE_JSON_UTF8);

    ObjectWriter writer = om.writer();
    if (indent) {
      DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
      pp.indentArraysWith(new DefaultIndenter("  ", DefaultIndenter.SYS_LF));
      writer = writer.with(pp);
    }

    writer.writeValue(response.getWriter(), jsonResponse);
  }

  protected boolean shouldIndent(HttpServletRequest request) {
    String parameter = request.getParameter(PARAM_INDENT);
    return YES.contains(parameter);
  }
}
