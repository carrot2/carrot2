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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.carrot2.HttpRequest;
import org.carrot2.HttpResponse;
import org.junit.Ignore;
import org.junit.Test;

public class DcsServiceSaturationTest extends AbstractDistributionTest {
  private static final String DCS_SHUTDOWN_TOKEN = "_shutdown_";

  // We can't configure Jetty reliably to cap maximum incoming connections. Leave this to
  // a proxy server on top of the clusterig service where this can be more reliably controlled.
  @Ignore
  @Test
  public void runSaturationTest() throws Exception {
    int maxThreads = 8;
    int connections = maxThreads + 20;

    DcsConfig config =
        new DcsConfig(getDistributionDir(), DCS_SHUTDOWN_TOKEN)
            .withMaxThreads(maxThreads)
            .withTestServlet(true);

    try (DcsService service = new ForkedDcs(config)) {
      ExecutorService executorService = Executors.newFixedThreadPool(connections);
      try {
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<HttpResponse>> futures =
            IntStream.range(0, connections)
                .mapToObj(
                    idx ->
                        executorService.submit(
                            () -> {
                              startLatch.await();
                              HttpResponse response =
                                  HttpRequest.builder()
                                      .queryParam("sleep", "" + TimeUnit.SECONDS.toMillis(2))
                                      .sendGet(service.getAddress().resolve("/service/test"));

                              System.out.println(
                                  idx
                                      + ": "
                                      + response.getStatusCode()
                                      + " "
                                      + response.bodyAsUtf8().trim());
                              return response;
                            }))
                .collect(Collectors.toList());
        startLatch.countDown();
        for (Future<HttpResponse> future : futures) {
          future.get();
        }
      } finally {
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
      }
    }
  }
}
