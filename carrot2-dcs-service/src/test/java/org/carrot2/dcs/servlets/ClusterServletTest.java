package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.carrot2.dcs.client.ClusterResponse;
import org.carrot2.math.mahout.Arrays;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ClusterServletTest extends AbstractServletTest {
  @Test
  public void testSimpleRequest() throws Exception {
    verifyRequest("simple.request.json", "simple.response.json");
  }

  @Test
  public void testTemplateRequest() throws Exception {
    setupMockTemplates("template1.json", "template2.json");

    when(request.getParameter(ClusterServlet.PARAM_TEMPLATE)).thenReturn("template1");
    verifyRequest("template.request.json", "template.response.json");
  }

  @Test
  public void testAttributeInRequest() throws Exception {
    verifyRequest("attrInRequest.request.json", "attrInRequest.response.json");
  }

  @Test
  public void testAttributeInTemplate() throws Exception {
    setupMockTemplates("template1.json", "template2.json");

    when(request.getParameter(ClusterServlet.PARAM_TEMPLATE)).thenReturn("template2");
    verifyRequest("attrInTemplate.request.json", "attrInTemplate.response.json");
  }

  private void verifyRequest(String requestResource, String responseResource) throws Exception {
    String requestData = resourceString(requestResource);
    log.debug("Request: " + requestData);

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);
    when(request.getInputStream()).thenReturn(new StringServletInputStream(requestData));

    doAnswer((a) -> {
      throw new RuntimeException("Unexpected sendError(): " + Arrays.toString(a.getArguments()));
    }).when(response).sendError(anyInt(), anyString());

    ClusterServlet servlet = new ClusterServlet();
    servlet.init(config);
    servlet.doPost(request, response);
    pw.flush();

    // Verify against expected response.
    String content = sw.toString();
    log.debug("Actual response: " + content);
    Assertions.assertThat(content).isEqualToIgnoringNewLines(resourceString(responseResource));

    // And try parsing against the client model.
    ObjectMapper om = new ObjectMapper();
    om.readValue(content, ClusterResponse.class);
  }
}

