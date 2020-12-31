/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.it;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Header;
import org.carrot2.HttpRequest;
import org.carrot2.HttpResponse;
import org.junit.Ignore;
import org.junit.Test;

public class DcsGzipTest extends AbstractDistributionTest {
  @Test
  @Ignore
  public void verifyGzipCompression() throws IOException {
    DcsConfig config =
        new DcsConfig(createTempDistMirror.mirrorPath(), AbstractDcsTest.DCS_SHUTDOWN_TOKEN)
            .withGzip(true);

    byte[] requestBytes = resourceBytes("large.request.json");

    try (DcsService service = new ForkedDcs(config)) {
      HttpResponse response =
          HttpRequest.builder()
              .header("Accept-Encoding", "gzip")
              .body(requestBytes)
              .sendPost(service.getAddress().resolve("/service/cluster"))
              .assertStatus(HttpServletResponse.SC_OK);

      for (Header header : response.getHeaders()) {
        System.out.println("> " + header);
      }

      System.out.println("# " + response.bodyAsUtf8().length());
      System.out.println(response.bodyAsUtf8());

      response.assertHeader("Content-Encoding", "gzip");
    }
  }
}
