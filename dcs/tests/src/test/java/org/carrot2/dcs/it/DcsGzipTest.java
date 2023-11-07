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
package org.carrot2.dcs.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.assertj.core.api.Assertions;
import org.carrot2.HttpRequest;
import org.carrot2.HttpResponse;
import org.carrot2.dcs.model.ClusterResponse;
import org.junit.Test;

public class DcsGzipTest extends AbstractDistributionTest {
  @Test
  public void verifyGzipCompression() throws IOException {
    DcsConfig config =
        new DcsConfig(getDistributionDir(), AbstractDcsTest.DCS_SHUTDOWN_TOKEN).withGzip(true);

    byte[] requestBytes = resourceBytes("large.request.json");

    try (DcsService service = new ForkedDcs(config)) {
      HttpResponse response =
          HttpRequest.builder()
              // We do want to know if the content was compressed, don't make
              // it transparent.
              .allowCompressedResponse(false)
              .header("Accept-Encoding", "gzip")
              .body(requestBytes)
              .sendPost(service.getAddress().resolve("/service/cluster"))
              .assertStatus(HttpServletResponse.SC_OK);

      // Ensure the response was returned compressed.
      response.assertHeader("Content-Encoding", "gzip");

      // Ensure we can read the response and it's valid.
      ObjectMapper om = new ObjectMapper();
      ClusterResponse resp =
          om.readValue(
              new GZIPInputStream(new ByteArrayInputStream(response.body())).readAllBytes(),
              ClusterResponse.class);
      Assertions.assertThat(resp.clusters).isNotEmpty();
    }
  }
}
