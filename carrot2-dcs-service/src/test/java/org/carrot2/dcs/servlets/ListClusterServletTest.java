package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.carrot2.math.mahout.Arrays;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ListClusterServletTest extends ServletTest {
  @Test
  public void testEmptyRequest() throws Exception {
    when(request.getParameter("indent")).thenReturn("true");

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);
    when(request.getInputStream()).thenReturn(requestStream("simple.request.json"));

    doAnswer((a) -> {
      throw new RuntimeException("Unexpected sendError(): " + Arrays.toString(a.getArguments()));
    }).when(response).sendError(anyInt(), anyString());

    ClusterServlet servlet = new ClusterServlet();
    servlet.init(config);
    servlet.doPost(request, response);
    pw.flush();

    String content = sw.toString();
    Assertions.assertThat(content).isEqualToIgnoringNewLines(response("simple.response.json"));
  }
}

