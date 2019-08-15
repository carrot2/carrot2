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
package org.carrot2.dcs.it;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.HttpHostConnectException;
import org.assertj.core.api.Assertions;
import org.carrot2.HttpRequest;
import org.junit.Test;

public class DcsServiceTest extends AbstractDistributionTest {
  @Test
  public void verifyListDefaults() throws IOException {
    try (DcsService service = startDcs()) {
      String body =
          HttpRequest.builder()
              .sendGet(service.getAddress().resolve("/service/list?indent"))
              .assertStatus(HttpServletResponse.SC_OK)
              .bodyAsUtf8();

      Assertions.assertThat(body)
          .isEqualToIgnoringWhitespace(resourceString("listDefaults.response.json"));

      Assertions.assertThat(
              HttpRequest.builder()
                  .sendGet(service.getAddress())
                  .assertStatus(HttpServletResponse.SC_OK)
                  .bodyAsUtf8())
          .contains("_welcome_file_test_marker_");
    }
  }

  @Test
  public void verifyShutdownToken() throws IOException, InterruptedException {
    try (DcsService service = startDcs()) {
      try {
        HttpRequest.builder()
            .queryParam("token", DCS_SHUTDOWN_TOKEN)
            .sendPost(service.getAddress().resolve("/shutdown"));
      } catch (NoHttpResponseException e) {
        // We don't care about the result of /shutdown call since
        // there's an internal race condition inside the shutdown handler's
        // code that may cause the request to be dropped.
      }

      for (int i = 4 * 5; --i >= 0; ) {
        Thread.sleep(250);

        try {
          HttpRequest.builder().sendGet(service.getAddress());
        } catch (HttpHostConnectException e) {
          // Ok, the service is down.
          return;
        }
      }

      Assertions.fail("Expected the service to shut down cleanly.");
      Assertions.assertThat(service.isRunning()).isFalse();
    }
  }
}
