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
package org.carrot2.dcs.examples;

import com.carrotsearch.console.jcommander.Parameter;
import com.carrotsearch.console.launcher.Command;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.Loggers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.List;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.carrot2.clustering.Cluster;
import org.carrot2.util.SuppressForbidden;

abstract class CommandScaffold extends Command<ExitCode> {
  public static final String ARG_DCS_URI = "--dcs";

  @Parameter(
      names = {ARG_DCS_URI},
      required = false,
      description = "DCS service's URI")
  public URI dcsService;

  @Override
  public final ExitCode run() {
    if (dcsService == null) {
      dcsService = URI.create("http://localhost:8080/service/");
      Loggers.CONSOLE.info(
          "Connecting to the default DCS address at: {} (use {} parameter to change)",
          dcsService,
          ARG_DCS_URI);
    } else {
      Loggers.CONSOLE.warn("Connecting to DCS at: {}", dcsService);
    }

    try (CloseableHttpClient httpClient =
        HttpClientBuilder.create()
            .disableAutomaticRetries()
            .disableContentCompression()
            .disableRedirectHandling()
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setMaxRedirects(0)
                    .setConnectionRequestTimeout(2000)
                    .setConnectTimeout(2000)
                    .build())
            .build()) {

      ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

      return run(httpClient, om);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected static void expect(CloseableHttpResponse httpResponse, int code) throws IOException {
    if (httpResponse.getStatusLine().getStatusCode() != code) {
      throw new IOException("Unexpected response: " + httpResponse.getStatusLine());
    }
  }

  abstract ExitCode run(CloseableHttpClient httpClient, ObjectMapper om) throws IOException;

  protected static <T> void printClusters(List<Cluster<T>> clusters) {
    printClusters(clusters, "");
  }

  @SuppressForbidden("Legitimate sysout to console.")
  private static <T> void printClusters(List<Cluster<T>> clusters, String indent) {
    for (Cluster<T> c : clusters) {
      System.out.println(indent + c);
      printClusters(c.getClusters(), indent + "  ");
    }
  }
}
