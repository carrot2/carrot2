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
import com.carrotsearch.console.launcher.Loggers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.dcs.model.ClusterRequest;
import org.carrot2.dcs.model.ClusterResponse;
import org.carrot2.dcs.model.ListResponse;

@Parameters(commandNames = "clusterWithParams")
public class E03_DcsClusterWithParams extends CommandScaffold {
  @Parameter(description = "Input data files for clustering (JSON).", required = true)
  public List<Path> inputs;

  @Override
  ExitCode run(CloseableHttpClient httpClient, ObjectMapper om) throws IOException {
    Attrs.toMap(new LingoClusteringAlgorithm());

    if (inputs.isEmpty()) {
      Loggers.CONSOLE.warn("Provide input JSON files with data to be sent to the DCS.");
      return ExitCodes.ERROR_INVALID_ARGUMENTS;
    }

    ListResponse config = getConfig(httpClient, om);
    Set<String> algorithms = config.algorithms.keySet();

    String requiredAlgorithm = LingoClusteringAlgorithm.NAME;
    String requiredLanguage = "English";
    if (!algorithms.contains(requiredAlgorithm)
        || !config.algorithms.get(requiredAlgorithm).contains(requiredLanguage)) {
      Loggers.CONSOLE.error("The DCS does not support the algorithm or language this example.");
      return ExitCodes.ERROR_INTERNAL;
    }

    for (Path input : inputs) {
      ClusterRequest request = new ClusterRequest();
      request.algorithm = requiredAlgorithm;
      request.language = requiredLanguage;

      // We know we're going to use the Lingo algorithm so we can configure it via the Java
      // API and extract the full set of attributes for the request.
      LingoClusteringAlgorithm preconfigured = new LingoClusteringAlgorithm();
      preconfigured.desiredClusterCount.set(5);
      preconfigured.queryHint.set("data mining");
      request.parameters = Attrs.extract(preconfigured);

      request.documents =
          om.readValue(
              Files.readAllBytes(input), new TypeReference<List<ClusterRequest.Document>>() {});

      RequestBuilder requestBuilder =
          RequestBuilder.post(dcsService.resolve("cluster"))
              .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
              .setEntity(
                  new ByteArrayEntity(
                      om.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)));

      try (CloseableHttpResponse httpResponse = httpClient.execute(requestBuilder.build())) {
        ClusterResponse response =
            ifValid(
                om,
                httpResponse,
                content ->
                    om.readValue(httpResponse.getEntity().getContent(), ClusterResponse.class));

        Loggers.CONSOLE.info("Clusters returned for file {}:", input);
        printClusters(response.clusters);
      }
    }

    return ExitCodes.SUCCESS;
  }

  private ListResponse getConfig(CloseableHttpClient httpClient, ObjectMapper om)
      throws IOException {
    HttpUriRequest request = RequestBuilder.get(dcsService.resolve("list")).build();
    try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
      return ifValid(
          om,
          httpResponse,
          content -> om.readValue(httpResponse.getEntity().getContent(), ListResponse.class));
    }
  }
}
