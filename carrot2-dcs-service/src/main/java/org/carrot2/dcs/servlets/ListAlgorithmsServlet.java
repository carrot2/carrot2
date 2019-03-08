package org.carrot2.dcs.servlets;

import org.carrot2.clustering.ClusteringAlgorithmProvider;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ListAlgorithmsServlet extends RestEndpoint {
  private ListAlgorithmsServletResponse defaultResponse;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    defaultResponse = new ListAlgorithmsServletResponse(defaults());
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    writeJsonResponse(response, shouldIndent(request), defaultResponse);
  }

  static List<String> defaults() {
    return StreamSupport.stream(ServiceLoader.load(ClusteringAlgorithmProvider.class).spliterator(), false)
        .map(p -> p.name())
        .sorted()
        .collect(Collectors.toList());
  }
}
