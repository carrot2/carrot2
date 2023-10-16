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
package org.carrot2.dcs.examples;

import com.carrotsearch.console.jcommander.Parameter;
import com.carrotsearch.console.jcommander.Parameters;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.console.launcher.Loggers;
import com.carrotsearch.console.launcher.ReportCommandException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.carrot2.dcs.model.ClusterRequest;
import org.carrot2.dcs.model.ClusterResponse;
import org.carrot2.dcs.model.ClusterServletParameters;
import org.carrot2.dcs.model.ListResponse;

@Parameters(commandNames = "cluster")
public class E02_DcsCluster extends CommandScaffold {
  @Parameter(names = "--algorithm", description = "The algorithm to use for clustering.")
  public String algorithm;

  @Parameter(names = "--language", description = "The language to use for clustering.")
  public String language;

  @Parameter(names = "--template", description = "The named template to use for clustering.")
  public String template;

  @Parameter(description = "Input data files for clustering (JSON).", required = true)
  public List<Path> inputs;

  @Override
  ExitCode run(CloseableHttpClient httpClient, ObjectMapper om) throws IOException {
    if (inputs.isEmpty()) {
      Loggers.CONSOLE.warn("Provide input JSON files with data to be sent to the DCS.");
      return ExitCodes.ERROR_INVALID_ARGUMENTS;
    }

    if ((algorithm == null || language == null) && template == null) {
      template = resolveFirstTemplate(httpClient, om);
    }

    for (Path input : inputs) {
      ClusterRequest request = new ClusterRequest();
      if (algorithm != null) {
        request.algorithm = algorithm;
      }
      if (language != null) {
        request.language = language;
      }

      if (!Files.isRegularFile(input)) {
        Loggers.CONSOLE.warn("This path is not a file, ignoring: {}", input);
        continue;
      }

      request.documents =
          om.readValue(
              Files.readAllBytes(input), new TypeReference<List<ClusterRequest.Document>>() {});

      RequestBuilder requestBuilder =
          RequestBuilder.post(dcsService.resolve("cluster"))
              .setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      if (template != null) {
        requestBuilder.addParameter(ClusterServletParameters.PARAM_TEMPLATE, template);
      }
      requestBuilder.setEntity(
          new ByteArrayEntity(om.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)));

      try (CloseableHttpResponse httpResponse = httpClient.execute(requestBuilder.build())) {
        ClusterResponse response =
            ifValid(
                om,
                httpResponse,
                content ->
                    om.readValue(httpResponse.getEntity().getContent(), ClusterResponse.class));

        Loggers.CONSOLE.info("Clusters returned for file {}:", input);
        printClusters(response.clusters);
        return ExitCodes.SUCCESS;
      }
    }

    return ExitCodes.SUCCESS;
  }

  private String resolveFirstTemplate(CloseableHttpClient httpClient, ObjectMapper om)
      throws IOException {
    HttpUriRequest request = RequestBuilder.get(dcsService.resolve("list")).build();
    try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
      ListResponse config =
          ifValid(
              om,
              httpResponse,
              content -> om.readValue(httpResponse.getEntity().getContent(), ListResponse.class));

      if (config.templates.isEmpty()) {
        Loggers.CONSOLE.error(
            "No templates declared in the DCS and optional parameters are empty.");
        throw new ReportCommandException(ExitCodes.ERROR_INVALID_ARGUMENTS);
      }

      String template = config.templates.keySet().iterator().next();
      Loggers.CONSOLE.info(
          "No algorithm or language specified. Using the first available template: {}", template);
      return template;
    }
  }

  public static void main(String[] args) {
    ExitCode exitCode = new Launcher().runCommand(new E02_DcsCluster(), args);
    System.exit(exitCode.processReturnValue());
  }
}
