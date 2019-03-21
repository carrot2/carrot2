package org.carrot2.dcs.it;

import org.assertj.core.api.Assertions;
import org.carrot2.HttpRequest;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DcsServiceTest extends AbstractDistributionTest {
  @Test
  public void listDefaults() throws IOException {
    try (DcsService service = startDcs()) {
      String body = HttpRequest.builder().sendGet(service.getAddress().resolve("/service/list?indent"))
          .assertStatus(HttpServletResponse.SC_OK)
          .bodyAsUtf8();

      Assertions.assertThat(body)
          .isEqualToIgnoringWhitespace(resourceString("listDefaults.response.json"));

      Assertions.assertThat(HttpRequest.builder().sendGet(service.getAddress())
          .assertStatus(HttpServletResponse.SC_OK)
          .bodyAsUtf8())
        .contains("_welcome_file_test_marker_");
    }
  }
}
