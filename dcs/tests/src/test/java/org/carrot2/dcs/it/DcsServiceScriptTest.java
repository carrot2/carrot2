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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.HttpHostConnectException;
import org.assertj.core.api.Assertions;
import org.carrot2.HttpRequest;
import org.junit.Test;

public class DcsServiceScriptTest extends AbstractDistributionTest {
  private static final String DCS_SHUTDOWN_TOKEN = "_shutdown_";

  @Test
  public void verifyShutdownToken() throws IOException, InterruptedException {
    try (DcsService service =
        new ForkedDcs(new DcsConfig(getDistributionDir(), DCS_SHUTDOWN_TOKEN))) {
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

  @Test
  public void verifyPidFileEmitted() throws IOException {
    Path pidFile = newTempFile();
    try (var service =
        new ForkedDcs(
            new DcsConfig(getDistributionDir(), DCS_SHUTDOWN_TOKEN).withPidFile(pidFile))) {

      Assertions.assertThat(pidFile).isRegularFile();
      var pid = Long.parseLong(Files.readString(pidFile, StandardCharsets.UTF_8));
      Assertions.assertThat(ProcessHandle.of(pid).get().isAlive()).isTrue();
    }
  }
}
