package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.carrot2.language.LanguageComponents;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.when;

public class ListLanguagesServletTest extends ServletTest {
  @Test
  public void testGet() throws Exception {
    when(request.getParameter("indent")).thenReturn("true");

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);

    ListLanguagesServlet servlet = new ListLanguagesServlet();
    servlet.init(config);
    servlet.doGet(request, response);
    pw.flush();

    ObjectMapper om = new ObjectMapper();
    Assertions.assertThat(om.readValue(sw.toString(), ListLanguagesServletResponse.class).languages)
        .containsOnlyElementsOf(LanguageComponents.languages());
  }
}