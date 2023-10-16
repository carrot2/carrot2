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

import com.carrotsearch.console.jcommander.Parameters;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.console.launcher.Loggers;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.dcs.model.ClusterRequest;
import org.carrot2.dcs.model.ClusterResponse;

/**
 * Demonstrates building a simple clustering request, populating algorithm attributes and parsing
 * the response using data model classes.
 */
@Parameters(commandNames = "dataModels")
public class E04_DcsDataModels extends CommandScaffold {
  @Override
  ExitCode run(CloseableHttpClient httpClient, ObjectMapper om) throws IOException {
    // fragment-start{build-request}
    LingoClusteringAlgorithm algorithm = new LingoClusteringAlgorithm();
    algorithm.preprocessing.phraseDfThreshold.set(1);
    algorithm.preprocessing.wordDfThreshold.set(1);

    ClusterRequest request = new ClusterRequest();
    request.algorithm = LingoClusteringAlgorithm.NAME;
    request.language = "English";
    request.parameters = Attrs.extract(algorithm);
    request.documents =
        Stream.of("foo bar", "bar", "baz")
            .map(
                value -> {
                  ClusterRequest.Document doc = new ClusterRequest.Document();
                  doc.setField("field", value);
                  return doc;
                })
            .collect(Collectors.toList());
    // fragment-end{build-request}

    RequestBuilder requestBuilder =
        RequestBuilder.post(dcsService.resolve("cluster"))
            .setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

    requestBuilder.setEntity(new ByteArrayEntity(om.writeValueAsBytes(request)));

    try (CloseableHttpResponse httpResponse = httpClient.execute(requestBuilder.build())) {
      ClusterResponse response =
          ifValid(
              om,
              httpResponse,
              content ->
                  om.readValue(httpResponse.getEntity().getContent(), ClusterResponse.class));

      Loggers.CONSOLE.info("Clusters returned:");
      printClusters(response.clusters);
    }

    return ExitCodes.SUCCESS;
  }

  public static void main(String[] args) {
    ExitCode exitCode = new Launcher().runCommand(new E04_DcsDataModels(), args);
    System.exit(exitCode.processReturnValue());
  }
}
