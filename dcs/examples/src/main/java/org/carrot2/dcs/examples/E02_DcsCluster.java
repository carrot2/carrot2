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
package org.carrot2.dcs.examples;

import com.carrotsearch.console.jcommander.Parameter;
import com.carrotsearch.console.jcommander.Parameters;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.console.launcher.Loggers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.carrot2.dcs.model.ClusterRequest;
import org.carrot2.dcs.model.ClusterResponse;

@Parameters(commandNames = "cluster")
public class E02_DcsCluster extends CommandScaffold {
  @Parameter(description = "Input data files for clustering (JSON).", required = true)
  public List<Path> inputs;

  @Override
  ExitCode run(CloseableHttpClient httpClient, ObjectMapper om) throws IOException {
    if (inputs.isEmpty()) {
      Loggers.CONSOLE.warn("Provide input JSON files with data to be sent to the DCS.");
      return ExitCodes.ERROR_INVALID_ARGUMENTS;
    }

    for (Path input : inputs) {
      ClusterRequest request = new ClusterRequest();
      request.algorithm = "Lingo";
      request.language = "English";
      request.documents =
          om.readValue(
              Files.readAllBytes(input), new TypeReference<List<ClusterRequest.Document>>() {});

      HttpUriRequest httpRequest =
          RequestBuilder.post(dcsService.resolve("cluster"))
              .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
              .setEntity(
                  new ByteArrayEntity(
                      om.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
              .build();

      try (CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {
        expect(httpResponse, HttpStatus.SC_OK);

        ClusterResponse response =
            om.readValue(httpResponse.getEntity().getContent(), ClusterResponse.class);

        Loggers.CONSOLE.info("Clusters returned for file {}:", input);
        printClusters(response.clusters);
      }
    }

    return ExitCodes.SUCCESS;
  }

  public static void main(String[] args) {
    ExitCode exitCode = new Launcher().runCommand(new E02_DcsCluster(), args);
    System.exit(exitCode.processReturnValue());
  }
}
